package com.daniel.infosumbar.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.daniel.infosumbar.R
import com.daniel.infosumbar.adapter.HistoryAdapter
import com.daniel.infosumbar.helper.AppPreferences
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.activity_history.*
import java.util.*
import kotlin.collections.ArrayList

class HistoryActivity : AppCompatActivity() {
    lateinit var adapter: HistoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val db = FirebaseFirestore.getInstance().collection("histori-transaksi")
        val f = ArrayList<DocumentSnapshot>()
        val appPreferences = AppPreferences(this)
        adapter = HistoryAdapter(f, 2)
        db.whereEqualTo("uid", "${appPreferences.uid}").addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                rv_history.layoutManager = LinearLayoutManager(this@HistoryActivity)
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
                adapter = HistoryAdapter(value?.documents!!, 2)
                Log.d("kjd", "sdfsdfd")
                rv_history.adapter = adapter
            }

        })
    }
}