package com.alqudri.infosumbar.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.alqudri.infosumbar.AnimationView
import com.alqudri.infosumbar.PdfActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.adapter.HistoryAdapter
import com.alqudri.infosumbar.utills.colorActive
import com.alqudri.infosumbar.utills.hideView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_booking_admin.*
import java.util.*

class BookingAdmin : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance().collection("db")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_admin)
        val animationView = AnimationView()
        animationView.init(fabPrint)
        terima.setOnClickListener {
            arrayListOf<Button>(proses, terima).colorActive(false)
            fabPrint.show()
            rv_terima.hideView(false)
            rv_proses.hideView(true)
        }

        proses.setOnClickListener {
            fabPrint.hide()
            arrayListOf<Button>(proses, terima).colorActive(true)
            rv_terima.hideView(true)
            rv_proses.hideView(false)
        }
        db.whereNotEqualTo("bukti_pembayaran", null).whereEqualTo("isConfirm", false).addSnapshotListener { value, error ->
            if(!value!!.isEmpty){
                rv_proses.layoutManager = LinearLayoutManager(this@BookingAdmin)
                rv_proses.adapter = HistoryAdapter(value?.documents!!, 1)
            }
        }

        db.whereEqualTo("isConfirm", true).addSnapshotListener { value, error ->
            if(!value!!.isEmpty){
                rv_terima.layoutManager = LinearLayoutManager(this@BookingAdmin)
                rv_terima.adapter = HistoryAdapter(value?.documents!!, 1)
            }
        }

        db.whereEqualTo("bukti_pembayaran", null).whereEqualTo("isBooked", true).addSnapshotListener { value, error ->
            for(doc: DocumentSnapshot in value!!.documents){
                val h = Date()
                val k = Date()
                h.time = doc?.get("timestamp").toString().toLong()
                val j = h.time - k.time
                Log.d("fores", "$j    $h    dan $k ${doc?.get("l")}")
                if(j<1){
                    db.document(doc.id).delete()
                }
            }
        }

        fabPrint.setOnClickListener {
            startActivity(Intent(this, PdfActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        db.whereNotEqualTo("bukti_pembayaran", null).whereEqualTo("isConfirm", false).addSnapshotListener { value, error ->
            if(!value!!.isEmpty){
                rv_proses.layoutManager = LinearLayoutManager(this@BookingAdmin)
                rv_proses.adapter = HistoryAdapter(value?.documents!!, 1)
            }
        }
    }
}