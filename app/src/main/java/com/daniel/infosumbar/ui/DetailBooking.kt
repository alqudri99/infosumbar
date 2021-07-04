package com.daniel.infosumbar.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.daniel.infosumbar.R
import com.daniel.infosumbar.adapter.BookingAdapter
import com.daniel.infosumbar.model.BookingModel
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

        val db = FirebaseFirestore.getInstance().collection("data-booking")
        val tanggal = intent.getStringExtra("tanggal")
        val jam = intent.getStringExtra("jam")
        val pilihan = intent.getStringExtra("jenis")
        status.text = pilihan?.toUpperCase()
        db.document("$tanggal").addSnapshotListener(object : EventListener<DocumentSnapshot>{
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                val list = ArrayList<BookingModel>()
                list.add(BookingModel("IGTV", "igtv"))
                list.add(BookingModel("Feed Post", "feed"))
                list.add(BookingModel("Instastory", "stories"))
                list.add(BookingModel("Instastory Visit Store", "instastory"))
                list.add(BookingModel("Highlight", "higlight"))
            rv_booking.layoutManager = LinearLayoutManager(baseContext)
            rv_booking.adapter = BookingAdapter(list, value!!, jam!!, pilihan!!, this@DetailBooking)
        }
    })
    }
}
