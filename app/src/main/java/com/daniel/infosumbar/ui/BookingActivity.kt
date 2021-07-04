package com.daniel.infosumbar.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.daniel.infosumbar.R
import com.daniel.infosumbar.helper.AppPreferences
import com.daniel.infosumbar.model.*
import com.daniel.infosumbar.utills.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import kotlinx.android.synthetic.main.dialog_jenis.*
import kotlinx.android.synthetic.main.dialog_single_checkout.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.HashMap


class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var pilihTanggal: String
    var jam: String? = null
    val INTENT_MODE = "intent_mode"
    val INTENT_PILIHAN = "intent_pilihan"
    var intentPilihan = ""

    val db = FirebaseFirestore.getInstance().collection("histori-transaksi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        val intentMode = intent.getStringExtra(INTENT_MODE)
        intentPilihan = intent.getStringExtra(INTENT_PILIHAN).toString()
        val hash = HashMap<String, DetailBook>()
        val db = FirebaseFirestore.getInstance().collection("data-booking")

        tanggal.text = "Hari Ini"
        hash.put("stories", DetailBook())
        hash.put("igtv", DetailBook())
        hash.put("feed", DetailBook())
        hash.put("instastory", DetailBook())
        hash.put("higlight", DetailBook())

        val sdf = SimpleDateFormat("dd-M-yyyy")
        val t = Date()
        val currentDate = sdf.format(t)
        pilihTanggal = currentDate
        val date = Data(
            "${currentDate}",
            DataValue(hash),
            DataValue(hash),
            DataValue(hash),
            DataValue(hash),
            DataValue(hash)
        )

        db.whereEqualTo("tanggal", "$currentDate")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (value?.documents!!.isEmpty()) {
                        db.document("${currentDate}").set(date)
                    }
                }

            })


        if (intentMode == "1") {
            pilihan.hideView(false)
            pilihan1.hideView(true)
            if (intentPilihan == "umum") {
                arrayListOf<Button>(bt1, bt2, bt3, bt4, bt5).disableButton()
            } else {
                arrayListOf<Button>(bt1, bt2, bt3, bt4, bt5).disableButton()
            }
        } else {
            placeholder.hideView(false)
            pilihan.hideView(true)

            instagram_jadwal.setOnClickListener {
                val intent = Intent(this, DetailBooking::class.java)
                if (jam != null) {
                    intent.putExtra("jam", jam)
                    intent.putExtra("jenis", intentPilihan)
                    intent.putExtra("tanggal", pilihTanggal)
                    startActivity(intent)
                    this.finish()
                }

            }

            buttonDeclare()


            tanggal.setOnClickListener {
                val date = Date()
                val cal = Calendar.getInstance(TimeZone.getDefault())
                val tahun = cal.get(Calendar.YEAR)
                SpinnerDatePickerDialogBuilder()
                    .context(this@BookingActivity)
                    .callback(this@BookingActivity)
                    .spinnerTheme(R.style.NumberPickerStyle)
                    .showTitle(true)
                    .showDaySpinner(true)
                    .defaultDate(tahun, date.month, date.date)
                    .maxDate(2030, 11, 25)
                    .minDate(tahun, date.month, date.date)
                    .build()
                    .show()
            }
        }

        instagram.setOnClickListener {
            val intent = Intent(this@BookingActivity, DetailPaketBooking::class.java)
            intent.putExtra(INTENT_MODE, "instagram")
            intent.putExtra(INTENT_PILIHAN, intentPilihan)
            finish()
            startActivity(intent)
        }

        post.setOnClickListener {
            showDialogPilihan(1)
        }

        igtv.setOnClickListener {
            showDialogPilihan(2)
        }


    }


    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val db = FirebaseFirestore.getInstance().collection("data-booking")
        db.whereEqualTo("tanggal", "$dayOfMonth-${monthOfYear + 1}-$year")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (value?.documents!!.isEmpty()) {
                        val hash = HashMap<String, DetailBook>()
                        hash.put("stories", DetailBook())
                        hash.put("igtv", DetailBook())
                        hash.put("feed", DetailBook())
                        hash.put("instastory", DetailBook())
                        hash.put("higlight", DetailBook())

                        val date = Data(
                            "$dayOfMonth-${monthOfYear + 1}-$year",
                            DataValue(hash),
                            DataValue(hash),
                            DataValue(hash),
                            DataValue(hash),
                            DataValue(hash)
                        )

                        db.document("$dayOfMonth-${monthOfYear + 1}-$year").set(date)
                        pilihTanggal = "$dayOfMonth-${monthOfYear + 1}-$year"
                        tanggal.text = "$dayOfMonth-${monthOfYear + 1}-$year"
                        Log.d("tanggal", "$dayOfMonth-${monthOfYear + 1}-$year")
                    } else {
                        pilihTanggal = "$dayOfMonth-${monthOfYear + 1}-$year"
                        tanggal.text = "$dayOfMonth-${monthOfYear + 1}-$year"
                    }
                }

            })
    }

    fun buttonDeclare(){
        bt1.setOnClickListener {
            arrayListOf<Button>(bt2, bt3, bt4, bt5).colorState(placeholder, pilihan1, bt1)
            insta.text = "Instagram"
//            twitter.text = "Twitter"
            jam = "jam9"
        }
        bt2.setOnClickListener {
            arrayListOf<Button>(bt1, bt3, bt4, bt5).colorState(placeholder, pilihan1, bt2)
            insta.text = "Instagram"
//            twitter.text = "Twitter - Booked"
            jam = "jam13"
        }
        bt3.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt4, bt5).colorState(placeholder, pilihan1, bt3)
            insta.text = "Instagram - Booked"
//            twitter.text = "Twitter - Booked"
            jam = "jam16"
        }
        bt4.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt3, bt5).colorState(placeholder, pilihan1, bt4)
            insta.text = "Instagram - Booked"
//            twitter.text = "Twitter - Booked"
            jam = "jam19"
        }
        bt5.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt3, bt4).colorState(placeholder, pilihan1, bt5)
            insta.text = "Instagram - Booked"
//            twitter.text = "Twitter - Booked"
            jam = "jam22"
        }
    }


    fun showDialogPilihan(mode: Int) {
        val dialog = Dialog(this)
        with(dialog){
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(R.layout.dialog_jenis)
            when(mode){
                1 -> {
                    val type = "facebook"
                    umum.text = "Foto"
                    umkm.text = "Video"
                    umum.setOnClickListener {
                        doSingleBooking("$type.${umum.text}", dialog)
                    }

                    umkm.setOnClickListener {
                        doSingleBooking("$type.${umkm.text}", dialog)
                    }
                }

                2 -> {
                    val type = "facebook"
                    umum.text = "Tweet"
                    umkm.text = "Fleet"
                    umum.setOnClickListener {
                        doSingleBooking("$type.${umum.text}", dialog)
                    }

                    umkm.setOnClickListener {
                        doSingleBooking("$type.${umkm.text}", dialog)
                    }
                }
            }


            dialog.show()
        }
    }

    fun doSingleBooking(type: String, dialog: Dialog){
        val appPreferences = AppPreferences(this)
        val time = Date()
        var tagihan = ""
//            appPreferences.jobs = dialog.edt_jobs.text.toString()
            time.hours = time.hours + 1

            FirebaseFirestore.getInstance().collection("harga")
                .document("instagram")
                .collection("instagram")
                .document(intentPilihan)
                .addSnapshotListener { value, error ->
//                    dialog.total.text = "" + value?.get(paket)
                    tagihan = "" + value?.get(type)
                }

            val gen = randomString(15)
            db.add(
                HistoriData(
                    gen,
                    "${time.time}",
                    true,
                    false,
                    "${appPreferences.uid}",
                    "${appPreferences.nama}",
                    "${appPreferences.email}",
                    "${appPreferences.nohp}",
                    "${appPreferences.jobs}",
                    "$type",
                    null,
                    tagihan
                )
            )
                .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                    override fun onComplete(p0: Task<DocumentReference>) {
                        dialog.cancel()
                        Toast.makeText(this@BookingActivity, "Berhasil Checkout", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@BookingActivity, DetailCheckout::class.java)
                        intent.putExtra("cek", "${p0.result?.id}")
                        startActivity(intent)
                    }

                })
    }

//    fun showDialogPilihan(
//        paket: String,
//        title: String,
//        jenis: BookingModel,
//        mode: String,
//        context: Context,
//        jamMode: String,
//        db: DocumentReference
//    ) {
//
//        Log.d("haha", "$paket ${jenis.nama} $mode $jamMode")
//        var tagihan = ""
//        val appPreferences = AppPreferences(context)
//        val dialog = Dialog(context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(true)
//        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setContentView(R.layout.dialog_single_checkout)
//        FirebaseFirestore.getInstance().collection("harga")
//            .document("instagram")
//            .collection("instagram")
//            .document("$mode")
//            .addSnapshotListener { value, error ->
//                if(jenis.prefix == "stories"){
//                    dialog.total.text = "${value!!["${jenis.prefix}"]}"
//                    tagihan = "${value!!["${jenis.prefix}"]}"
//                }else{
//                    dialog.total.text = "${value!!["$jamMode.${jenis.prefix}"]}"
//                    tagihan = "${value!!["$jamMode.${jenis.prefix}"]}"
//                }
//            }
//
//
//        dialog.checkout.setOnClickListener {
//            if(true){
//
//                val date = Date()
//                date.hours = date.hours +1
//                db.update("$jam.instagram.${jenis.prefix}.timestamp", date.time)
//                db.update("$jam.instagram.${jenis.prefix}.isBooked", true)
//                db.update("$jam.instagram.${jenis.prefix}.uid", appPreferences.uid)
//                Toast.makeText(
//                    context,
//                    "Slot Ini Berhasil Di Booking",
//                    Toast.LENGTH_SHORT
//                ).show()
//                val gen = TransNumGenerator(15, ThreadLocalRandom.current())
//                FirebaseFirestore.getInstance().collection("histori-transaksi").add(
//                    HistoriData(
//                        gen.nextString(),
//                        "${date.time}",
//                        true,
//                        false,
//                        "${appPreferences.uid}",
//                        "${appPreferences.nama}",
//                        "${appPreferences.email}",
//                        "${appPreferences.nohp}",
//                        ""+appPreferences.jobs,
//                        "${if(true)  "" else ""} ${jenis.nama} -$mode-",
//                        null,
//                        tagihan,
//                        true,
//                        db.id,
//                        "$jam.instagram.${jenis.prefix}"
//                    )
//                )
//                    .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
//                        override fun onComplete(p0: Task<DocumentReference>) {
//                            p0.result?.id?.let { it1 -> Log.d("fuck", it1) }
//                            val intent = Intent(context, DetailCheckout::class.java)
//                            intent.putExtra("cek", "${p0.result?.id}")
//                            Log.d("testIntent", "${p0.result?.id}")
//                            startActivity(intent)
//                        }
//
//                    })
//            }
//        }
//        dialog.show()
//    }
}

