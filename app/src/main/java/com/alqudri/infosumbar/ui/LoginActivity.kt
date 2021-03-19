package com.alqudri.infosumbar.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.helper.AppPreferences
import com.alqudri.infosumbar.model.userData
import com.alqudri.infosumbar.ui.admin.AdminHome
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_detail_checkout.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import kotlinx.android.synthetic.main.dialog_login.*
import java.util.*


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private var signInButton: SignInButton? = null
    private var googleApiClient: GoogleApiClient? = null
    var name: String? = null
    var email: String? = null
    var idToken: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseAuth = FirebaseAuth.getInstance()
        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        authStateListener = AuthStateListener { firebaseAuth ->
            // Get signedIn user
            val user = firebaseAuth.currentUser

            //if user is signed in, we call a helper method to save the user details to Firebase
            if (user != null) {
                // User is signed in
                // you could place other firebase code
                //logic to save the user details to Firebase

//                FirebaseFirestore.getInstance().collection("userdata").document("${user.uid}").set(
//                    userData(
//                        user.displayName,
//                        user.email,
//                        user.uid,
//                        user.photoUrl.toString(),
//                        user.phoneNumber,
//                        user.providerId
//                    )
//                )
                Log.d(
                    TAG,
                    "onAuthStateChanged:signed_in:" + user.uid
                )
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
        }
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //you can also use R.string.default_web_client_id
                .requestEmail()
                .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        btnLogin.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                val intent: Intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                startActivityForResult(intent,
                    RC_SIGN_IN
                )
            }
        })
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            idToken = account!!.idToken
            name = account.displayName
            email = account.email
            // you can store user data to SharedPreference
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuthWithGoogle(credential)
        } else {
            // Google Sign In failed, update UI appropriately
            Log.e(TAG, "Login Unsuccessful. $result")
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(credential: AuthCredential) {
        firebaseAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                Log.d(
                    TAG,
                    "signInWithCredential:onComplete:" + task.isSuccessful
                )
                if (task.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT)
                        .show()
                    if (firebaseAuth?.currentUser != null){
                        showDialogPilihan(firebaseAuth?.currentUser!!)
                    }

                } else {
                    Log.w(
                        TAG,
                        "signInWithCredential" + task.exception!!.message
                    )
                    task.exception!!.printStackTrace()
                    Toast.makeText(
                        this@LoginActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun gotoProfile() {
        val appPreferences = AppPreferences(this)
        FirebaseFirestore.getInstance().collection("admindata").document("data")
            .addSnapshotListener { value, error ->
                val adminList =  value?.get("admin") as List<String>
                if (adminList.contains(appPreferences.uid)){
                    val intent = Intent(this@LoginActivity, AdminHome::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this@LoginActivity, HomaActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

    }

    override fun onStart() {
        super.onStart()
        if (authStateListener != null) {
            FirebaseAuth.getInstance().signOut()
        }
        firebaseAuth!!.addAuthStateListener(authStateListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (authStateListener != null) {
            firebaseAuth!!.removeAuthStateListener(authStateListener!!)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 1
    }

    fun showDialogPilihan(user: FirebaseUser) {
        val appPreferences = AppPreferences(this)
        val dialog = Dialog(this)
        val intent = Intent(this, BookingActivity::class.java)
        var state = true
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_login)
        if(appPreferences.nohp == null){
            state = true
            dialog.keterangan.text = "Silahkan menginput Nomor Telepon Kamu"
            dialog.edt_nohp.visibility = View.VISIBLE
            dialog.tv_nohp.visibility = View.GONE
            dialog.tidak.visibility = View.GONE
        }else{
            state = false
            dialog.keterangan.text = "Apakah betul ini nomor kamu yang aktif? :"
            dialog.tv_nohp.text = appPreferences.nohp
            dialog.edt_nohp.visibility = View.GONE
            dialog.tv_nohp.visibility = View.VISIBLE
            dialog.tidak.visibility = View.VISIBLE
        }

        dialog.tidak.setOnClickListener {
            state = true
            dialog.keterangan.text = "Silahkan menginput Nomor Telepon Kamu"
            dialog.edt_nohp.visibility = View.VISIBLE
            dialog.tv_nohp.visibility = View.GONE
            dialog.tidak.visibility = View.GONE
        }
        dialog.konfirmasi.setOnClickListener {
            if (state){
                appPreferences.email = user.email
                appPreferences.nama = user.displayName
                appPreferences.uid = user.uid
                appPreferences.nohp = dialog.edt_nohp.text.toString()
                appPreferences.avatar = user.photoUrl.toString()
            }else{
                appPreferences.avatar = user.photoUrl.toString()
                appPreferences.email = user.email
                appPreferences.nama = user.displayName
                appPreferences.uid = user.uid
            }
            FirebaseFirestore.getInstance().collection("userdata").document("${user.uid}").set(
                userData(
                    user.displayName,
                    user.email,
                    user.uid,
                    user.photoUrl.toString(),
                    user.providerId
                )
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    dialog.cancel()
                    gotoProfile()
                }
            }
        }

        dialog.close.setOnClickListener {
            dialog.cancel()
        }
        dialog.show()
    }
}
