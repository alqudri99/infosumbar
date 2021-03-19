package com.alqudri.infosumbar.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.helper.AppPreferences
import com.alqudri.infosumbar.model.HistoriData
import com.alqudri.infosumbar.utills.TransNumGenerator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_paket_booking.*
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class DetailPaketBooking : AppCompatActivity() {
    val INTENT_MODE = "intent_mode"
    val INTENT_PILIHAN = "intent_pilihan"
    val db = FirebaseFirestore.getInstance().collection("db")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_paket_booking)
        val intentPilihan = intent.getStringExtra(INTENT_PILIHAN)
        val intentMode = intent.getStringExtra(INTENT_MODE)
        paket1.setOnClickListener {
            if (intentPilihan != null && intentMode != null) {
                showDialogPilihan(
                    "paket1",
                    "Paket 1",
                    arrayListOf("12", "3", "1"),
                    intentPilihan,
                    intentMode
                )
            }
        }
        paket2.setOnClickListener {
            if (intentPilihan != null && intentMode != null) {
                showDialogPilihan(
                    "paket2",
                    "Paket 2",
                    arrayListOf("7", "2", "1"),
                    intentPilihan,
                    intentMode
                )
            }
        }
        paket3.setOnClickListener {
            if (intentPilihan != null && intentMode != null) {
                showDialogPilihan(
                    "paket3",
                    "Paket 3",
                    arrayListOf("5", "2", "0"),
                    intentPilihan,
                    intentMode
                )
            }
        }
    }


    fun showDialogPilihan(
        paket: String,
        title: String,
        jumlah: List<String>,
        jenis: String,
        mode: String
    ) {

        Log.d("haha", "$paket $jenis $mode")
        var tagihan = ""
        val appPreferences = AppPreferences(this)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_detail_paket)
        dialog.title.text = title
        dialog.feed.text = jumlah.get(0) + " x"
        dialog.story.text = jumlah.get(1) + " x"
        dialog.website.text = jumlah.get(2) + " x"
        val time = Date()
        FirebaseFirestore.getInstance().collection("harga")
            .document(mode)
            .collection(mode)
            .document(jenis)
            .addSnapshotListener { value, error ->
                dialog.total.text = "" + value?.get(paket)
                tagihan = "" + value?.get(paket)
            }

        if (appPreferences.jobs != null){
            dialog.edt_jobs.setText(appPreferences.jobs)
        }

        dialog.checkout.setOnClickListener {
            if(dialog.edt_jobs.text.isNotEmpty()){
                appPreferences.jobs = dialog.edt_jobs.text.toString()
                time.hours = time.hours + 1

                val gen = TransNumGenerator(15, ThreadLocalRandom.current())
                db.add(
                    HistoriData(
                        gen.nextString(),
                        "${time.time}",
                        true,
                        false,
                        "${appPreferences.uid}",
                        "${appPreferences.nama}",
                        "${appPreferences.email}",
                        "${appPreferences.nohp}",
                        dialog.edt_jobs.text.toString(),
                        "$mode $title -$jenis-",
                        null,
                        tagihan
                    )
                )
                    .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                        override fun onComplete(p0: Task<DocumentReference>) {
                            p0.result?.id?.let { it1 -> Log.d("fuck", it1) }
                            val intent = Intent(this@DetailPaketBooking, DetailCheckout::class.java)
                            intent.putExtra("cek", "${p0.result?.id}")
                            finish()
                            startActivity(intent)
                        }

                    })
            }else{
                dialog.error.visibility = View.VISIBLE
            }
        }
        dialog.show()
    }
}

