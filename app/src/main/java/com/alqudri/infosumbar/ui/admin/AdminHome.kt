package com.alqudri.infosumbar.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.helper.AppPreferences
import com.alqudri.infosumbar.ui.LoginActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_admin_home.*
import kotlinx.android.synthetic.main.activity_admin_home.komplit
import kotlinx.android.synthetic.main.activity_admin_home.logoutBtn
import kotlinx.android.synthetic.main.activity_admin_home.single
import kotlinx.android.synthetic.main.activity_homa.*

class AdminHome : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var gso: GoogleSignInOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        single.setOnClickListener {
            startActivity(Intent(this, AdminSetting ::class.java))
        }

        komplit.setOnClickListener {
            startActivity(Intent(this, BookingAdmin::class.java))
        }

        logoutBtn.setOnClickListener {
            gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
                .build()
            FirebaseAuth.getInstance().signOut()
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                object : ResultCallback<Status?> {
                    override fun onResult(status: Status) {
                        if (status.isSuccess()) {
                            val appPreferences = AppPreferences(this@AdminHome)
                            appPreferences.uid = null
                            startActivity(Intent(this@AdminHome, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Session not close",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }
}