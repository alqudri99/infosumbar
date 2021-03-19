package com.alqudri.infosumbar.ui.admin

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.utills.hideView
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
        val db = FirebaseFirestore.getInstance().collection("db")
        if (intent != null) {
            db.document(intent).addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                    if (value!!["bukti_pembayaran"] != null) {
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
                                        "oi bangsat den alah masan yo tadi atas nama ${value["nama"]}, iko email den aaa ${value["email"]}, iko nomor transaksinyo *${value["no_transaksi"]}*, dan tagihan yang alah den bayia sagiko nyo ${value["tagihan"]}, nomor den ang tanyo lai? iko nyo ${value["no_hp"]}",
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