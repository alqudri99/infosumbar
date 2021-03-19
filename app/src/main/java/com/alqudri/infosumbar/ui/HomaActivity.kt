package com.alqudri.infosumbar.ui

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.alqudri.infosumbar.PdfActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.helper.AppPreferences
import com.alqudri.infosumbar.ui.admin.AdminHome
import com.alqudri.infosumbar.utills.loadRounded
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_homa.*
import kotlinx.android.synthetic.main.dialog_jenis.*
import kotlinx.android.synthetic.main.dialog_terms_condition.*
import java.net.URLEncoder

class HomaActivity : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener {
    val INTENT_MODE = "intent_mode"
    val INTENT_PILIHAN = "intent_pilihan"
    private var googleApiClient: GoogleApiClient? = null
    private var gso: GoogleSignInOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homa)
        val appPreferences = AppPreferences(this)

        avatar.loadRounded(appPreferences.avatar)
//
//        FirebaseFirestore.getInstance().collection("harga").document("jam9").set()
        FirebaseFirestore.getInstance().collection("data")

        termsbt.setOnClickListener {
            showDialog()
        }

        komplit.setOnClickListener {
            showDialogPilihan("1")
        }

        single.setOnClickListener {
            showDialogPilihan("2")
        }

        historis.setOnClickListener{
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        logoutBtn.setOnClickListener {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
                .build()
            FirebaseAuth.getInstance().signOut()
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                object : ResultCallback<Status?> {
                    override fun onResult(status: Status) {
                        if (status.isSuccess()) {
                            val appPreferences = AppPreferences(this@HomaActivity)
                            appPreferences.uid = null
                            startActivity(Intent(this@HomaActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Session not close",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        }

        map.setOnClickListener {
            lokasi("-0.899391","100.346870")
        }
    }

    fun lokasi(lat: String, long:String){
//        var intent = Intent(Intent.MAP)
        val intent = Uri.parse("google.navigation:q="+ lat + "," + long + "&mode=b")
        val mapIntent = Intent(Intent.ACTION_VIEW, intent)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }


    fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_terms_condition)

        dialog.show()
        dialog.selesai.setOnClickListener {
            dialog.cancel()
        }

    }

    fun showDialogPilihan(intentMode: String) {
        val dialog = Dialog(this)
        val intent = Intent(this, BookingActivity::class.java)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_jenis)
        dialog.umum.setOnClickListener {
            Log.d("hjshjshjdhjs", "kjsdjdshfsd")
            intent.putExtra(INTENT_MODE, intentMode)
            intent.putExtra(INTENT_PILIHAN, "umum")
            startActivity(intent)
            dialog.cancel()
        }

        dialog.umkm.setOnClickListener {
            intent.putExtra(INTENT_MODE, intentMode)
            intent.putExtra(INTENT_PILIHAN, "umkm")
            startActivity(intent)
            dialog.cancel()
        }

        dialog.show()
//        dialog.selesai.setOnClickListener {
//            dialog.cancel()
//        }

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}