package com.daniel.infosumbar.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Window
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daniel.infosumbar.R
import com.daniel.infosumbar.helper.AppPreferences
import com.daniel.infosumbar.model.HistoriData
import com.daniel.infosumbar.model.userData
import com.daniel.infosumbar.utills.close
import com.daniel.infosumbar.utills.loadRounded
import com.daniel.infosumbar.utills.makeString
import com.daniel.infosumbar.utills.randomString
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_homa.*
import kotlinx.android.synthetic.main.dialog_biodata.*
import kotlinx.android.synthetic.main.dialog_gaji.*
import kotlinx.android.synthetic.main.dialog_jenis.*
import kotlinx.android.synthetic.main.dialog_login.keterangan
import kotlinx.android.synthetic.main.dialog_login.konfirmasi
import kotlinx.android.synthetic.main.dialog_terms_condition.*
import java.io.File
import java.util.*

class HomaActivity : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener {
    lateinit var appPreferences: AppPreferences
    val INTENT_MODE = "intent_mode"
    val INTENT_PILIHAN = "intent_pilihan"
    private var googleApiClient: GoogleApiClient? = null
    private var gso: GoogleSignInOptions? = null
    val db = FirebaseFirestore.getInstance().collection("histori-transaksi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homa)
        appPreferences = AppPreferences(this)

        val intent = Intent(this, BookingActivity::class.java)
//
//        FirebaseFirestore.getInstance().collection("harga").document("jam9").set()
//        FirebaseFirestore.getInstance().collection("data-booking")

        termsbt.setOnClickListener {
            showDialog()
        }

        singlePack.setOnClickListener {
            showPilihan()
        }

        komplit.setOnClickListener {
            val intent = Intent(this@HomaActivity, DetailPaketBooking::class.java)
            intent.putExtra(INTENT_MODE, "instagram")
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

        dialog.close_login_p.setOnClickListener{
            dialog.cancel()
        }
        dialog.status.text = if(appPreferences.jenis.equals("umum")) "corporate" else appPreferences.jenis
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
        val dialog2 = Dialog(this)
        var initState = true
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog2.setCancelable(true)
        dialog2.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog2.setContentView(R.layout.dialog_biodata)
dialog2.konfirmasi.text = "Edit"
        dialog2.title.text = "Daftar"
//        dialog2.status.text = appPreferences.jenis
        dialog2.keterangan.text = "Silahkan menginput Data Kamu"
//        dialog2.edt_gaji.visibility = View.VISIBLE
        var uid = ""

        dialog2.disablePassword_r.setOnClickListener {
            if(initState){
                dialog2.edt_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                initState = false
                dialog2.edt_password.setSelection(dialog2.edt_password.text.toString().length)
                dialog2.disablePassword_r.setImageResource(R.drawable.ic_baseline_blur_off_24)
            }else{
                dialog2.edt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                initState = true
                dialog2.edt_password.setSelection(dialog2.edt_password.text.toString().length)
                dialog2.disablePassword_r.setImageResource(R.drawable.ic_baseline_blur_on_24)
            }
        }
        var gajiValue = 0

        dialog2.rg_gar.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when(checkedId){
                    R.id.rb_umkm ->{
                        gajiValue = 30000000
                    }

                    R.id.rb_umum ->{
                        gajiValue = 31100000
                    }
                }
            }

        })
        FirebaseFirestore.getInstance().collection("data-pengguna").document("${appPreferences.uid}").get()
            .addOnSuccessListener {it2 ->
                it2["sallary"]?.let{
                    gajiValue = "$it".toInt()
                }
                Log.d("asasasa", "$gajiValue")
                if(gajiValue < 31100000){
                    dialog2.rg_gar.check(R.id.rb_umkm)
                }else{
                    dialog2.rg_gar.check(R.id.rb_umum)
                }

                it2["email"]?.let{dialog2.edt_email .setText(it.toString())}
                it2["job"]?.let{dialog2.edt_pekerjaan .setText(it.toString())}
                it2["name"]?.let{dialog2.edt_namaa .setText(it.toString())}
                it2["password"]?.let{dialog2.edt_password .setText(it.toString())}
                it2["no_hp"]?.let{dialog2.edt_no_hp .setText(it.toString())}
                uid = it2["uid"].toString()
            }

        dialog2.konfirmasi.setOnClickListener {
            var isDone = false
            with(dialog2){
                Log.d("adasa", "${edt_pekerjaan.text}  ${edt_password.text}  ${edt_namaa.text}")
            }
//            if (dialog2.edt_gaji.text.isNotEmpty()) {
                val gaji = gajiValue
                if (gaji > 30100000) {
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
                                            sallary = gajiValue.toString(),
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

//            } else {
//                Toast.makeText(this, "Input Gaji Terlebih Dahulu!", Toast.LENGTH_SHORT).show()
//            }
        }

        dialog2.close_login.setOnClickListener {
            dialog2.cancel()
        }
        dialog2.show()
    }
    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    fun showPilihan() {
        val dialog = Dialog(this)
        with(dialog){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_jenis)
            close_login_p.close(this)
                    umum.text = "Facebook"
                    umkm.text = "Twitter"
                    umum.setOnClickListener {
                        showDialogPilihan(1)
                    }

            status.text = if(appPreferences.jenis.equals("umum")) "corporate" else appPreferences.jenis
                    umkm.setOnClickListener {
                        showDialogPilihan(2)
                    }

            dialog.show()
        }
    }



    fun showDialogPilihan(mode: Int) {
        val dialog = Dialog(this)
        with(dialog){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_jenis)

            close_login_p.close(this)
            dialog.status.text = if(appPreferences.jenis.equals("umum")) "corporate" else appPreferences.jenis
            when(mode){
                1 -> {
                    val type = "facebook"
                    umum.text = "Foto"
                    umkm.text = "Video"
                    umum.setOnClickListener {
                        doSingleBooking("$type.${umum.text}".toLowerCase(), dialog)
                    }

                    umkm.setOnClickListener {
                        doSingleBooking("$type.${umkm.text}".toLowerCase(), dialog)
                    }
                }

                2 -> {
                    val type = "twitter"
                    umum.text = "Tweet"
                    umkm.text = "Fleet"
                    umum.setOnClickListener {
                        doSingleBooking("$type.${umum.text}".toLowerCase(), dialog)
                    }

                    umkm.setOnClickListener {
                        doSingleBooking("$type.${umkm.text}".toLowerCase(), dialog)
                    }
                }
            }


            dialog.show()
        }
    }



    fun doSingleBooking(type: String, dialog: Dialog){
        val appPreferences = AppPreferences(this)
        val time = Date()
        var tagihan = ""
//            appPreferences.jobs = dialog.edt_jobs.text.toString()
        time.hours = time.hours + 1

        FirebaseFirestore.getInstance().collection("harga")
            .document("instagram")
            .collection("instagram")
            .document("${appPreferences.jenis}")
            .addSnapshotListener { value, error ->
//                    dialog.total.text = "" + value?.get(paket)
                tagihan = "" + value?.get(type)
            }

        val gen = randomString(15)
        db.add(
            HistoriData(
                gen,
                "${time.time}",
                true,
                false,
                "${appPreferences.uid}",
                "${appPreferences.nama}",
                "${appPreferences.email}",
                "${appPreferences.nohp}",
                "${appPreferences.jobs}",
                "$type",
                null,
                tagihan
            )
        )
            .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                override fun onComplete(p0: Task<DocumentReference>) {
                    dialog.cancel()
                    Toast.makeText(this@HomaActivity, "Berhasil Checkout", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@HomaActivity, DetailCheckout::class.java)
                    intent.putExtra("cek", "${p0.result?.id}")
                    startActivity(intent)
                }

            })
    }
}