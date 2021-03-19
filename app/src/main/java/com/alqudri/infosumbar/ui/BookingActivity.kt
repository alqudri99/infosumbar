package com.alqudri.infosumbar.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.alqudri.infosumbar.AnimationView
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.model.Data
import com.alqudri.infosumbar.model.DataValue
import com.alqudri.infosumbar.model.DetailBook
import com.alqudri.infosumbar.utills.colorState
import com.alqudri.infosumbar.utills.disableButton
import com.alqudri.infosumbar.utills.hideView
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.tsongkha.spinnerdatepicker.DatePicker
import com.tsongkha.spinnerdatepicker.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_booking.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class BookingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    lateinit var pilihTanggal: String
    var jam: String? = null
    val INTENT_MODE = "intent_mode"
    val INTENT_PILIHAN = "intent_pilihan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        val intentMode = intent.getStringExtra(INTENT_MODE)
        val intentPilihan = intent.getStringExtra(INTENT_PILIHAN)
        val hash = HashMap<String, DetailBook>()
        val db = FirebaseFirestore.getInstance().collection("data")

        tanggal.text = "Hari Ini"
        hash.put("stories", DetailBook())
        hash.put("igtv", DetailBook())
        hash.put("feed", DetailBook())

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


    }


    override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val db = FirebaseFirestore.getInstance().collection("data")
        db.whereEqualTo("tanggal", "$dayOfMonth-${monthOfYear + 1}-$year")
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (value?.documents!!.isEmpty()) {
                        val hash = HashMap<String, DetailBook>()
                        hash.put("stories", DetailBook())
                        hash.put("igtv", DetailBook())
                        hash.put("feed", DetailBook())

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
            twitter.text = "Twitter"
            jam = "jam9"
        }
        bt2.setOnClickListener {
            arrayListOf<Button>(bt1, bt3, bt4, bt5).colorState(placeholder, pilihan1, bt2)
            insta.text = "Instagram"
            twitter.text = "Twitter - Booked"
            jam = "jam13"
        }
        bt3.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt4, bt5).colorState(placeholder, pilihan1, bt3)
            insta.text = "Instagram - Booked"
            twitter.text = "Twitter - Booked"
            jam = "jam16"
        }
        bt4.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt3, bt5).colorState(placeholder, pilihan1, bt4)
            insta.text = "Instagram - Booked"
            twitter.text = "Twitter - Booked"
            jam = "jam19"
        }
        bt5.setOnClickListener {
            arrayListOf<Button>(bt1, bt2, bt3, bt4).colorState(placeholder, pilihan1, bt5)
            insta.text = "Instagram - Booked"
            twitter.text = "Twitter - Booked"
            jam = "jam22"
        }
    }
}

