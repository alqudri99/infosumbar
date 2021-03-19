package com.alqudri.infosumbar.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.utills.hideView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_detail_checkout.*
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import java.net.URLEncoder
import java.util.*

class DetailCheckout : AppCompatActivity() {
    var file: Uri? = null
    var state: String? = null
    var backState = true
    lateinit var countDownTimer: CountDownTimer
    val db = FirebaseFirestore.getInstance().collection("db")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_checkout)
        val db = FirebaseFirestore.getInstance().collection("db")
        val intent = getIntent().getStringExtra("cek")
        countDownTimer = object : CountDownTimer(1, 1) {
            override fun onFinish() {
            }

            override fun onTick(p0: Long) {

            }
        }
        load.hideView(true)
        hubun.hideView(true)
        FirebaseFirestore.getInstance().collection("admindata").document("data")
            .addSnapshotListener { value, error ->
                textView7.text = "" + value?.get("no_rekening")
            }
        if (intent != null) {
            val b =  db.document(intent)
            b.addSnapshotListener(object : EventListener<DocumentSnapshot> {
                override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {

                    hubun.setOnClickListener {
                        val packageManager: PackageManager = getPackageManager()
                        val i = Intent(Intent.ACTION_VIEW)

                        try {
                            val url =
                                "https://api.whatsapp.com/send?phone=" + "+62812-7575-7509" + "&text=" + URLEncoder.encode(
                                    "Hay damin  nama saya *${value!!["nama"]}*, saya baru saja menyelaseaikan pembayaran, dengan" +
                                            " nomor transaksi *${value["no_transaksi"]}* sebesar *Rp ${value["tagihan"]}*, tolong di cek ya, oh ya jika damin punya info lebih lanjut mengenai hal ini, damin dapat menghubungi saya melalui email di *${value["email"]}*, dan dapat menelepon saya melalui nomor ini *${value["no_hp"]}*, Makasih ya. \n(Tolong hiraukan pesan ini, ini adalah pesan percobaan Aplikasi User Side Info Sumbar Ads Booking App)",
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

                    backState = true
                    state = value?.get("bukti_pembayaran") as String?
                    val h = Date()
                    val k = Date()
                    val an = Date()
                    if(value?.get("timestamp") != null){
                        h.time = value?.get("timestamp").toString().toLong()
                    }


                    if (value?.get("bukti_pembayaran") != null){
                        load.hideView(true)
                        hubun.hideView(false)
                        bayar.hideView(true)
                        upload.scaleType = ImageView.ScaleType.CENTER_CROP
                        lay.minimumHeight = 300
                        Glide.with(baseContext).load(value?.get("bukti_pembayaran").toString()).into(upload)
                    }
                    an.hours = h.hours - k.hours
                    an.minutes = h.minutes - k.minutes
                    an.seconds = h.seconds - k.minutes
                    var jam =
                        ((h.hours.toLong() * 1000) * 60 * 60) - ((k.hours.toLong() * 1000) * 60 * 60)
                    val jd =
                        ((h.hours.toLong() * 1000) * 60 * 60) + ((h.minutes.toLong() * 1000) * 60) + (h.seconds.toLong() * 1000)
                    val jq =
                        ((k.hours.toLong() * 1000) * 60 * 60) + ((k.minutes.toLong() * 1000) * 60) + (k.seconds.toLong() * 1000)
                    val j = jd - jq
                    Log.d("timee", "$j")

                    val g = h.time - k.time
                    countDownTimer = object : CountDownTimer(g, 1000) {
                        override fun onFinish() {
                            db.document(intent).update("isBooked", false)
                        }

                        override fun onTick(p0: Long) {
                            textView9.text =
                                "${((p0 / (1000 * 60 * 60)) % 24)}:${((p0 / (1000 * 60)) % 60)}:${(p0 / 1000) % 60}"
                        }

                    }

                    countDownTimer.start()

                    tv_tagihan.text = "" + value?.get("tagihan")
                }
            })


        }


        upload_bt.setOnClickListener{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else{
                    //system OS is < Marshmallow
                    pickImageFromGallery();
                }

        }


    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            file = data?.data
            load.hideView(false)
            file?.let { uploadImage(it) }
        }


    }


    override fun onPause() {
        super.onPause()
        countDownTimer.cancel()
    }

    override fun onStop() {
        super.onStop()
        countDownTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer.cancel()
    }

    private fun uploadImage(filePath: Uri){
        if(filePath != null){
            val ref = FirebaseStorage.getInstance().getReference("data")?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)
backState = false
           uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadUri = it.result
                    db.document(intent.getStringExtra("cek")!!).update("bukti_pembayaran", "$downloadUri")
                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{

            }
        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        if(backState){
           super.onBackPressed()
        }else{
            Toast.makeText(this, "Upload Belum Selesai!!!", Toast.LENGTH_SHORT)
        }
    }

}