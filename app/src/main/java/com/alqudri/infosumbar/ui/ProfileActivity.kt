package com.alqudri.infosumbar.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alqudri.infosumbar.R
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.OptionalPendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth


class ProfileActivity : AppCompatActivity(),
    GoogleApiClient.OnConnectionFailedListener {
    var logoutBtn: Button? = null
    var userName: TextView? = null
    var userEmail: TextView? = null
    var userId: TextView? = null
    var profileImage: ImageView? = null
    private var googleApiClient: GoogleApiClient? = null
    private var gso: GoogleSignInOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        logoutBtn = findViewById(R.id.logoutBtn)
        userName = findViewById(R.id.name)
        userEmail = findViewById(R.id.email)
        userId = findViewById(R.id.userId)
        profileImage = findViewById(R.id.profileImage)
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso!!)
            .build()
        logoutBtn?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                FirebaseAuth.getInstance().signOut()
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    object : ResultCallback<Status?> {
                        override fun onResult(status: Status) {
                            if (status.isSuccess()) {
                                gotoMainActivity()
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
        })
    }

    override fun onStart() {
        super.onStart()
        val opr: OptionalPendingResult<GoogleSignInResult> =
            Auth.GoogleSignInApi.silentSignIn(googleApiClient)
        if (opr.isDone) {
            val result = opr.get()
            handleSignInResult(result)
        } else {
            opr.setResultCallback(object : ResultCallback<GoogleSignInResult?> {
                override fun onResult(googleSignInResult: GoogleSignInResult) {
                    handleSignInResult(googleSignInResult)
                }
            })
        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            userName!!.text = account!!.displayName
            userEmail!!.text = account.email
            userId!!.text = account.id
            try {
                Glide.with(this).load(account.photoUrl).into(profileImage!!)
            } catch (e: NullPointerException) {
                Toast.makeText(applicationContext, "image not found", Toast.LENGTH_LONG).show()
            }
        } else {
            gotoMainActivity()
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
}