 package com.daniel.infosumbar.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daniel.infosumbar.R
import com.daniel.infosumbar.`interface`.DialogListener
import com.daniel.infosumbar.helper.AppPreferences
import com.daniel.infosumbar.model.userData
import com.daniel.infosumbar.ui.admin.AdminHome
import com.daniel.infosumbar.utills.*
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
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_detail_checkout.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_biodata.*
import kotlinx.android.synthetic.main.dialog_biodata.edt_email
import kotlinx.android.synthetic.main.dialog_biodata.edt_password
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import kotlinx.android.synthetic.main.dialog_gaji.*
import kotlinx.android.synthetic.main.dialog_gaji.edt_gaji
import kotlinx.android.synthetic.main.dialog_login.*
import kotlinx.android.synthetic.main.dialog_login.close
import kotlinx.android.synthetic.main.dialog_login.keterangan
import kotlinx.android.synthetic.main.dialog_login.konfirmasi
import kotlinx.android.synthetic.main.dialog_login.tidak
import kotlinx.android.synthetic.main.dialog_login_info.*
import kotlinx.android.synthetic.main.dialog_main_login.*
import java.util.*


class LoginActivity : AppCompatActivity() {
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

        btnLogin.setOnClickListener{
                login()
            }

        btnSignUp.setOnClickListener{
                signUp()
            }
    }


    fun signUp() {
//        val appPreferences = AppPreferences(this)
//        val dialog2 = Dialog(this)
//        var state = true
//        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog2.setCancelable(true)
//        dialog2.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog2.setContentView(R.layout.dialog_biodata)
//
//        dialog2.keterangan.text = "Silahkan menginput Data Kamu"
//        dialog2.edt_gaji.visibility = View.VISIBLE
//
////        FirebaseFirestore.getInstance().collection("data-pengguna").document("${user.uid}").get()
////            .addOnSuccessListener {
////                it["sallary"]?.let{dialog2.edt_gaji.setText(it.toString())}
////                it["job"]?.let{dialog2.edt_pekerjaan .setText(it.toString())}
////                it["name"]?.let{dialog2.edt_namaa .setText(it.toString())}
////                it["password"]?.let{dialog2.edt_password .setText(it.toString())}
////            }
//
//        dialog2.konfirmasi.setOnClickListener {
//            var isDone = false
//            with(dialog2){
//                Log.d("adasa", "${edt_pekerjaan.text}  ${edt_password.text}  ${edt_namaa.text}")
//            }
//            if (dialog2.edt_gaji.text.isNotEmpty()) {
//                val gaji = dialog2.edt_gaji.text.toString().toInt()
//                if (gaji > 5000000) {
//                    appPreferences.jenis = "umum"
//                } else {
//                    appPreferences.jenis = "umkm"
//                }
//                FirebaseFirestore.getInstance().collection("data-pengguna").whereEqualTo("email", dialog2.edt_email.makeString())
//                    .addSnapshotListener(object : EventListener<QuerySnapshot> {
//                        override fun onEvent(
//                            value: QuerySnapshot?,
//                            error: FirebaseFirestoreException?
//                        ) {
//                            val data = value!!.documents
//                            Log.d("aassa", "data : ${data.size}")
//                            if (data.size == 0 && !isDone) {
//                                isDone = true
//                                val uid = randomString(40)
//                                FirebaseFirestore.getInstance().collection("data-pengguna").document(uid)
//                                    .set(
//                                        userData(
//                                            name = dialog2.edt_namaa.makeString(),
//                                            email = dialog2.edt_email.makeString(),
//                                            uid = uid,
//                                            job = dialog2.edt_pekerjaan.makeString(),
//                                            password = dialog2.edt_password.makeString(),
//                                            sallary = dialog2.edt_gaji.makeString(),
//                                            no_hp = dialog2.edt_no_hp.makeString()
//                                        )
//                                    ).addOnCompleteListener() {
//                                        with(dialog2){
//                                            dialog2.cancel()
//                                            login()
//                                            Toast.makeText(baseContext, "Pendaftaran Berhasil!, silahkan Login", Toast.LENGTH_LONG).show()
//                                        }
//                                        if (it.isSuccessful) {
////                        showDialogPilihan(user)
//                                        } else {
//                                            Toast.makeText(baseContext, "Gagal Daftar", Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                            }else Toast.makeText(baseContext, "Telah terdaftar sebelumnya, Silahkan Login!", Toast.LENGTH_SHORT).show()
//
//                        }
//
//                    })
//
//            } else {
//                Toast.makeText(this, "Input Gaji Terlebih Dahulu!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        dialog2.close.cancel(dialog2)
//        dialog2.show()
    }

    fun login(){
//        var userSet = FirebaseUser

        var initState = false
        val appPreferences = AppPreferences(this)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_login_info)

        dialog.disablePassword.setOnClickListener {
            if(initState){
                dialog.edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                initState = false
                dialog.edt_password.setSelection(dialog.edt_password.text.toString().length)
                dialog.disablePassword.setImageResource(R.drawable.ic_baseline_blur_off_24)
            }else{
                dialog.edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                initState = true
                dialog.edt_password.setSelection(dialog.edt_password.text.toString().length)
                dialog.disablePassword.setImageResource(R.drawable.ic_baseline_blur_on_24)
            }
        }
        dialog.btn_login.setOnClickListener {it2 ->
            val password = dialog.edt_password_login_info.makeString()
            var email = dialog.edt_email_login_info.makeString()

            FirebaseFirestore.getInstance().collection("data-pengguna").whereEqualTo("email", email).get().addOnCompleteListener {
                val data = it.result!!
                val user = userData(
                    name = data.queryToString("name"),
                    uid = data.queryToString("uid"),
                    job = data.queryToString("job"),
                    sallary = data.queryToString("sallary"),
                    no_hp = data.queryToString("no_hp")
                )
                Log.d("tas", it.result!!?.queryToString("password") +" " + email +"  d :" +data.documentChanges.size)
                if(password.equals(it.result!!?.queryToString("password"))){
                    appPreferences.email = user.email
                    appPreferences.nama = user.name
                    appPreferences.uid = user.uid
                    appPreferences.nohp = user.no_hp
                    appPreferences.jobs = user.job
                    val gaji = if(user.sallary!!.isNotEmpty()) user.sallary!!.toInt() else 0
                    if (gaji > 5000000) {
                        appPreferences.jenis = "umum"
                    } else {
                        appPreferences.jenis = "umkm"
                    }

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
                }else{
                    Toast.makeText(baseContext, "Password/Username Salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.btn_sign_up.setOnClickListener {
            signUp()
        }
        dialog.close_info.cancel(dialog)
        dialog.show()
    }

}
