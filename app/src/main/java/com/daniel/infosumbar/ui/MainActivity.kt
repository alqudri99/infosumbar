package com.daniel.infosumbar.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.daniel.infosumbar.R
import com.daniel.infosumbar.helper.AppPreferences
import com.daniel.infosumbar.ui.admin.AdminHome
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

}}

