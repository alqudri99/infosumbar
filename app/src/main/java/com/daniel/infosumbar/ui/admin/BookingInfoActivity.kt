package com.daniel.infosumbar.ui.admin

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daniel.infosumbar.R
import com.daniel.infosumbar.utills.hideView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_booking_info.*
import java.net.URLEncoder


class BookingInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_info)

        val intent = getIntent().getStringExtra("cek")
        val db = FirebaseFirestore.getInstance().collection("histori-transaksi")
        if (intent != null) {
            db.document(intent).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (value?.get("bukti_pembayaran") != null) {
                        Glide.with(baseContext).load(value?.get("bukti_pembayaran") as String)
                            .into(bukti)
                        val isSingle = value["isSingle"] as Boolean
                        val isConfirm = value["isConfirm"] as Boolean
                        jenis.text = value["jenis_iklan"] as String
                        nama.text = value["nama"] as String
                        hp.text = value["no_hp"] as String
                        tagihan.text = value["tagihan"] as String
                        kerja.text = value["jenis_pekerjaan"] as String
                        email.text = value["email"] as String

                        if (isConfirm) {
                            tolak.hideView(true)
                            terima.hideView(true)
                        } else {
                            tolak.hideView(false)
                            terima.hideView(false)
                        }
                        terima.setOnClickListener {
                            if (isSingle) {
                                val docRef = value["singleDocumentReference"] as String
                                val fieldRoute = value["fieldRoute"] as String
                                FirebaseFirestore.getInstance().collection("data").document(docRef)
                                    .update("$fieldRoute.isConfirm", true)
                                db.document(intent).update("isConfirm", true)
                            } else {
                                db.document(intent).update("isConfirm", true)
                            }
                        }

                        tolak.setOnClickListener {
                            if (isSingle) {
                                val docRef = value["singleDocumentReference"] as String
                                val fieldRoute = value["fieldRoute"] as String
                                FirebaseFirestore.getInstance().collection("data").document(docRef)
                                    .update("$fieldRoute.isBooked", false)
                                db.document(intent).delete()
                            } else {
                                db.document(intent).delete()
                            }
                        }

                        hub.setOnClickListener {
                            val packageManager: PackageManager = getPackageManager()
                            val i = Intent(Intent.ACTION_VIEW)

                            try {
                                val url =
                                    "https://api.whatsapp.com/send?phone=" + "+62812-7575-7509" + "&text=" + URLEncoder.encode(
                                        "Hai, orderan kamu untuk promosi di infoSumbar sudah kami konfirmasi atas nama *${value["nama"]}* email/no hp *${value["email"]}*/*${value["no_hp"]}* kode transaksi *${value["no_transaksi"]}* jumlah tagihan *${value["tagihan"]}* Orderan " +
                                                "kamu sudah kami masukan ke list booking iklan di infoSumbar. Terima kasih.",
                                        "UTF-8"
                                    )
                                i.setPackage("com.whatsapp")
                                i.data = Uri.parse(url)
                                if (i.resolveActivity(packageManager) != null) {
                                    startActivity(i)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }
                    } else {
                        Toast.makeText(baseContext, "Berhasil Manghapus Data", Toast.LENGTH_SHORT)
                            .show()
                        startActivity(Intent(this@BookingInfoActivity, AdminHome::class.java))
                        finish()
                    }
                }

            })


        }

    }
}