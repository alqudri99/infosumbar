package com.alqudri.infosumbar.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.helper.AppPreferences
import com.alqudri.infosumbar.ui.admin.AdminHome
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            val appPreferences = AppPreferences(this)
            if(appPreferences.uid != null){
                val appPreferences = AppPreferences(this)
                FirebaseFirestore.getInstance().collection("admindata").document("data")
                    .addSnapshotListener { value, error ->
                        val adminList =  value?.get("admin") as List<String>
                        if (adminList.contains(appPreferences.uid)){
                            val intent = Intent(this@MainActivity, AdminHome::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }else{
                            val intent = Intent(this@MainActivity, HomaActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }

        }, 2000)
//        db.document("f").addSnapshotListener(object : EventListener<DocumentSnapshot>{
//            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
//                bt.text = "${value?.get("data")}"
//                bt.isClickable = value?.get("isActive") as Boolean
//            }
//
//        })
//
//        sw.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
//            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
//                if(p1){
//                    db.document("f").set(Data())
//                }else{
//                    db.document("f").set(Data(false, "Booked"))
//                }
//            }
//
//        })
//
//        bt.setOnClickListener {
////            db.document("f").set(Data(false, "Booked"))
//            startActivity(Intent(this@MainActivity, BookingActivity::class.java))
//        }
//    }
}}

