package com.alqudri.infosumbar.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.adapter.BookingAdapter
import com.alqudri.infosumbar.model.BookingModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.android.synthetic.main.activity_detail_booking.*
import java.util.*

class DetailBooking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_booking)

        val db = FirebaseFirestore.getInstance().collection("data")
        val tanggal = intent.getStringExtra("tanggal")
        val jam = intent.getStringExtra("jam")
        val pilihan = intent.getStringExtra("jenis")
        db.document("$tanggal").addSnapshotListener(object : EventListener<DocumentSnapshot>{
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                val list = ArrayList<BookingModel>()
                list.add(BookingModel("IGTV", "igtv"))
                list.add(BookingModel("Feed Post", "feed"))
                list.add(BookingModel("Stories", "stories"))
            rv_booking.layoutManager = LinearLayoutManager(baseContext)
            rv_booking.adapter = BookingAdapter(list, value!!, jam!!, pilihan!!, this@DetailBooking)
        }
    })
    }
}
