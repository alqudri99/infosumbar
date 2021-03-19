package com.alqudri.infosumbar.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.utills.layoutBind
import com.alqudri.infosumbar.utills.layoutBindUmum
import com.alqudri.infosumbar.utills.saveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_admin_setting.*
import kotlinx.android.synthetic.main.activity_detail_paket_booking.view.*
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import kotlinx.android.synthetic.main.include_harga.view.*
import kotlinx.android.synthetic.main.include_harga.view.jenis_pengguna
import kotlinx.android.synthetic.main.include_paket.view.*
import kotlinx.android.synthetic.main.include_single_ig.view.*

class AdminSetting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_setting)
        val db = FirebaseFirestore.getInstance()
        val hargaUmkm = findViewById<LinearLayout>(R.id.harga_umkm)
        val hargaUmum = findViewById<LinearLayout>(R.id.harga_umum)
        val paketUmkm = findViewById<LinearLayout>(R.id.paket_umkm)
        val paketUmum = findViewById<LinearLayout>(R.id.paket_umum)

        val jam1 = findViewById<LinearLayout>(R.id.jam1)
        val jam2 = findViewById<LinearLayout>(R.id.jam2)
        val jam3 = findViewById<LinearLayout>(R.id.jam3)
        val jam4 = findViewById<LinearLayout>(R.id.jam4)
        val jam5 = findViewById<LinearLayout>(R.id.jam5)
        db.collection("admindata").document("data").addSnapshotListener { value, error ->
            namarek.setText("${value!!["an"]}")
            norek.setText("${value!!["no_rekening"]}")
        }

        db.collection("harga")
            .document("instagram")
            .collection("instagram")
            .document("umkm")
            .addSnapshotListener { value, error ->
                hargaUmkm.jenis_pengguna.text = "UMKM"
                hargaUmkm.edt_twitter.setText("${value!!["twitter"]}")
                hargaUmkm.edt_fb_foto.setText("${value!!["facebook.foto"]}")
                hargaUmkm.edt_fb_video.setText("${value!!["facebook.video"]}")

                paketUmkm.jenis_pengguna1.text = "UMKM"
                paketUmkm.edt_paket1.setText("${value["paket1"]}")
                paketUmkm.edt_paket2.setText("${value["paket2"]}")
                paketUmkm.edt_paket3.setText("${value["paket3"]}")

                val jam = arrayListOf<String>("9:00", "13:00", "16:00", "19:00", "22:00")
                val prefix = arrayListOf<String>("jam9", "jam13", "jam16", "jam19", "jam22")
                val layout = arrayListOf<LinearLayout>(jam1, jam2, jam3, jam4, jam5)
                layout.layoutBind(value, prefix, "UMKM", jam)
            }

        db.collection("harga")
            .document("instagram")
            .collection("instagram")
            .document("umum")
            .addSnapshotListener { value, error ->
                hargaUmum.jenis_pengguna.text = "Umum"
                hargaUmum.edt_twitter.setText("${value!!["twitter"]}")
                hargaUmum.edt_fb_video.setText("${value!!["facebook.video"]}")
                hargaUmum.edt_fb_foto.setText("${value!!["facebook.foto"]}")

                paketUmum.jenis_pengguna1.text = "Umum"
                paketUmum.edt_paket1.setText("${value["paket1"]}")
                paketUmum.edt_paket2.setText("${value["paket2"]}")
                paketUmum.edt_paket3.setText("${value["paket3"]}")

                val jam = arrayListOf<String>("9:00", "13:00", "16:00", "19:00", "22:00")
                val prefix = arrayListOf<String>("jam9", "jam13", "jam16", "jam19", "jam22")
                val layout = arrayListOf<LinearLayout>(jam1, jam2, jam3, jam4, jam5)
                layout.layoutBindUmum(value, prefix, "Umum", jam)
            }

        simpan.setOnClickListener {
            val layout = arrayListOf<LinearLayout>(jam1, jam2, jam3, jam4, jam5)
            db.collection("admindata").document("data").update("an", "${namarek.text}")
            db.collection("admindata").document("data").update("no_rekening", "${norek.text}")
            layout.saveData(db, hargaUmkm, paketUmkm)
            layout.saveData(db, hargaUmum, paketUmum)
        }
    }
}