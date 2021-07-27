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
import androidx.recyclerview.widget.LinearLayoutManager
import com.daniel.infosumbar.R
import com.daniel.infosumbar.`interface`.DisableRecylerView
import com.daniel.infosumbar.adapter.BookingAdapter
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
import kotlinx.android.synthetic.main.activity_booking.rv_booking
import kotlinx.android.synthetic.main.activity_detail_booking.*
import kotlinx.android.synthetic.main.dialog_detail_paket.*
import kotlinx.android.synthetic.main.dialog_jenis.*
import kotlinx.android.synthetic.main.dialog_single_checkout.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.HashMap


class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var pilihTanggal: String

    val dbl = FirebaseFirestore.getInstance().collection("data-booking")
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
        hash.put("highlight", DetailBook())

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

//            instagram_jadwal.setOnClickListener {
//                val intent = Intent(this, DetailBooking::class.java)
//                if (jam != null) {
//                    intent.putExtra("jam", jam)
//                    intent.putExtra("jenis", intentPilihan)
//                    intent.putExtra("tanggal", pilihTanggal)
//                    startActivity(intent)
//                    this.finish()
//                }
//
//            }



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
                        hash.put("highlight", DetailBook())

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

    fun initFirebase(){
        statuss.text = (if(intentPilihan.equals("umum")) "corporate" else intentPilihan).toUpperCase()
        dbl.document("$pilihTanggal").addSnapshotListener(object : EventListener<DocumentSnapshot>{
            override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
                val list = ArrayList<BookingModel>()
                val linearLayoutManager = object : LinearLayoutManager(this@BookingActivity){ override fun canScrollVertically(): Boolean { return false } }
                list.add(BookingModel("IGTV", "igtv"))
                list.add(BookingModel("Feed Post", "feed"))
                list.add(BookingModel("Instastory", "stories"))
                list.add(BookingModel("Instastory Visit Store", "instastory"))
                list.add(BookingModel("Highlight", "highlight"))
                rv_booking.layoutManager = LinearLayoutManager(baseContext)
//                rv_booking.setHasFixedSize(true)
//                rv_booking.setNestedScrollingEnabled(false);
//                rv_booking.addOnItemTouchListener(DisableRecylerView())
//                rv_booking.addOnItemTouchListener(object : View.OnTouchListener{})
                rv_booking.adapter = BookingAdapter(list, value!!, jam!!, intentPilihan!!, this@BookingActivity)
            }
        })
    }
    fun buttonDeclare(){
        bt1.setOnClickListener {
            arrayListOf<Button>(bt2, bt3, bt4, bt5).colorState(placeholder, pilihan1, bt1)
            insta.text = "Instagram"
//            twitter.text = "Twitter"
            jam = "jam9"
            initFirebase()
        }
        bt2.setOnClickListener {
            arrayListOf<Button>(bt1, bt3, bt4, bt5).colorState(placeholder, pilihan1, bt2)
            insta.text = "Instagram"
//            twitter.text = "Twitter - Booked"
            jam = "jam13"
            initFirebase()
        }
        bt3.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt4, bt5).colorState(placeholder, pilihan1, bt3)
            insta.text = "Instagram - Booked"
//            twitter.text = "Twitter - Booked"
            jam = "jam16"
            initFirebase()
        }
        bt4.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt3, bt5).colorState(placeholder, pilihan1, bt4)
            insta.text = "Instagram - Booked"
//            twitter.text = "Twitter - Booked"
            jam = "jam19"
            initFirebase()
        }
        bt5.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt3, bt4).colorState(placeholder, pilihan1, bt5)
            insta.text = "Instagram - Booked"
//            twitter.text = "Twitter - Booked"
            jam = "jam22"
            initFirebase()
        }
    }

}

