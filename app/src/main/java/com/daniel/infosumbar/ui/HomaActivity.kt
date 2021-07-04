package com.daniel.infosumbar.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import com.daniel.infosumbar.R
import com.daniel.infosumbar.helper.AppPreferences
import com.daniel.infosumbar.model.userData
import com.daniel.infosumbar.utills.loadRounded
import com.daniel.infosumbar.utills.makeString
import com.daniel.infosumbar.utills.randomString
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_homa.*
import kotlinx.android.synthetic.main.dialog_biodata.*
import kotlinx.android.synthetic.main.dialog_gaji.*
import kotlinx.android.synthetic.main.dialog_gaji.edt_gaji
import kotlinx.android.synthetic.main.dialog_jenis.*
import kotlinx.android.synthetic.main.dialog_login.*
import kotlinx.android.synthetic.main.dialog_login.close
import kotlinx.android.synthetic.main.dialog_login.keterangan
import kotlinx.android.synthetic.main.dialog_login.konfirmasi
import kotlinx.android.synthetic.main.dialog_terms_condition.*

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

        val intent = Intent(this, BookingActivity::class.java)
        avatar.loadRounded(appPreferences.avatar)
//
//        FirebaseFirestore.getInstance().collection("harga").document("jam9").set()
//        FirebaseFirestore.getInstance().collection("data-booking")

        termsbt.setOnClickListener {
            showDialog()
        }

        komplit.setOnClickListener {
            intent.putExtra(INTENT_MODE, "1")
            intent.putExtra(INTENT_PILIHAN, appPreferences.jenis)
            startActivity(intent)
        }

        single.setOnClickListener {
            intent.putExtra(INTENT_MODE, "2")
            intent.putExtra(INTENT_PILIHAN, appPreferences.jenis)
            startActivity(intent)
        }

        historis.setOnClickListener{
            startActivity(Intent(this, HistoryActivity::class.java))
        }


        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
            .build()

        logoutBtn.setOnClickListener {
            showDialogPilihan()
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

    fun showDialogPilihan() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_jenis)
        dialog.umum.setOnClickListener {
            val appPreferences = AppPreferences(this@HomaActivity)
            appPreferences.uid = null
            startActivity(Intent(this@HomaActivity, LoginActivity::class.java))
            finish()
        }

        dialog.umkm.setOnClickListener {
            changeBio()
            dialog.cancel()
        }

        dialog.show()
//        dialog.selesai.setOnClickListener {
//            dialog.cancel()
//        }

    }
    fun changeBio() {
        val appPreferences = AppPreferences(this)
        val dialog2 = Dialog(this)
        var state = true
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.setCancelable(true)
        dialog2.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2.setContentView(R.layout.dialog_biodata)

        dialog2.keterangan.text = "Silahkan menginput Data Kamu"
        dialog2.edt_gaji.visibility = View.VISIBLE
        var uid = ""
        FirebaseFirestore.getInstance().collection("data-pengguna").document("${appPreferences.uid}").get()
            .addOnSuccessListener {
                it["sallary"]?.let{dialog2.edt_gaji.setText(it.toString())}
                it["job"]?.let{dialog2.edt_pekerjaan .setText(it.toString())}
                it["name"]?.let{dialog2.edt_namaa .setText(it.toString())}
                it["password"]?.let{dialog2.edt_password .setText(it.toString())}
                it["no_hp"]?.let{dialog2.edt_password .setText(it.toString())}
                uid = it["no_hp"].toString()
            }

        dialog2.konfirmasi.setOnClickListener {
            var isDone = false
            with(dialog2){
                Log.d("adasa", "${edt_pekerjaan.text}  ${edt_password.text}  ${edt_namaa.text}")
            }
            if (dialog2.edt_gaji.text.isNotEmpty()) {
                val gaji = dialog2.edt_gaji.text.toString().toInt()
                if (gaji > 5000000) {
                    appPreferences.jenis = "umum"
                } else {
                    appPreferences.jenis = "umkm"
                }

                                FirebaseFirestore.getInstance().collection("data-pengguna").document(uid)
                                    .set(
                                        userData(
                                            name = dialog2.edt_namaa.makeString(),
                                            email = dialog2.edt_email.makeString(),
                                            uid = uid,
                                            job = dialog2.edt_pekerjaan.makeString(),
                                            password = dialog2.edt_password.makeString(),
                                            sallary = dialog2.edt_gaji.makeString(),
                                            no_hp = dialog2.edt_no_hp.makeString()
                                        )
                                    ).addOnCompleteListener() {
                                        with(dialog2){
                                            dialog2.cancel()
//                                            login()
                                            Toast.makeText(baseContext, "Edit Biodata Berhasil", Toast.LENGTH_LONG).show()
                                        }
                                        if (it.isSuccessful) {
//                        showDialogPilihan(user)
                                        } else {
                                            Toast.makeText(baseContext, "Gagal Edit", Toast.LENGTH_LONG).show()
                                        }
                                    }

            } else {
                Toast.makeText(this, "Input Gaji Terlebih Dahulu!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog2.close.setOnClickListener {
            dialog2.cancel()
        }
        dialog2.show()
    }
    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}