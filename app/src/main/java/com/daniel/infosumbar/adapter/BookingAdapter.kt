package com.daniel.infosumbar.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.daniel.infosumbar.R
import com.daniel.infosumbar.helper.AppPreferences
import com.daniel.infosumbar.model.BookingModel
import com.daniel.infosumbar.model.HistoriData
import com.daniel.infosumbar.ui.DetailBooking
import com.daniel.infosumbar.ui.DetailCheckout
import com.daniel.infosumbar.utills.TransNumGenerator
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_booking.view.*
import kotlinx.android.synthetic.main.dialog_single_checkout.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom


class BookingAdapter(var list: List<BookingModel>, var doc: DocumentSnapshot, var jam: String, var pilihan: String, var con: Context) :
    RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appPreferences = AppPreferences(itemView.context)
        fun bind(booking: BookingModel) {
            itemView.insta.text = "${booking.nama}"
            val tanggal = doc["tanggal"]
            val db = FirebaseFirestore.getInstance().collection("data-booking").document("$tanggal")
            val isBooked = doc["$jam.instagram.${booking.prefix}.isBooked"] as Boolean
            val isConfirm = doc["$jam.instagram.${booking.prefix}.isConfirm"] as Boolean
            val uid = doc["$jam.instagram.${booking.prefix}.uid"]
            var jamString = ""
            itemView.insta
            if (isBooked && isConfirm) {
                itemView.insta.text = "${booking.nama} (Booked)"
            } else if (isBooked && !isConfirm) {
                val g = Date()
                val b = Date()
                val jamm = doc["$jam.instagram.${booking.prefix}.timestamp"]

                if (jamm != null ) {
                    g.time = doc["$jam.instagram.${booking.prefix}.timestamp"] as Long
                    val time = g.time - b.time
                    if (!(time < 1)) {
                        object : CountDownTimer(time, 1000) {
                            override fun onFinish() {
                                itemView.insta.text = "${booking.nama}"
                                db.update("$jam.instagram.${booking.prefix}.isBooked", false)
                            }

                            override fun onTick(p0: Long) {
                                if (uid != null) {
                                    val uidNotNull = uid as String
                                    if (uidNotNull != appPreferences.uid) {
                                        itemView.insta.text =
                                            "${booking.nama} - Slot bebas dalam  ${((p0 / (1000 * 60 * 60)) % 24)}:${((p0 / (1000 * 60)) % 60)}:${(p0 / 1000) % 60}"
                                    } else if (isBooked && isConfirm) {
                                        itemView.insta.text = "${booking.nama} (Telah Dikonfirmasi Admin)"
                                    } else if (isBooked) {
                                        itemView.insta.text =
                                            "${booking.nama} (Silahkan Menyelesaikan Pembayaran"
                                    }
                                }
                            }

                        }.start()
                    } else {
                        db.update("$jam.instagram.${booking.prefix}.timestamp", null)
                    }
                }
            }

            itemView.stories.setOnClickListener {
                if (isBooked && isConfirm) {
                    Toast.makeText(
                        itemView.context,
                        "Slot Ini Telah Di Booking",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (isBooked && !isConfirm) {
                    Toast.makeText(
                        itemView.context,
                        "Slot ini telah di Booking, Sedang Di Proses Oleh Admin. Coba beberapa saat lagig",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {


                    showDialogPilihan("", "Konfirmasi", booking, pilihan, itemView.context,  jam, db)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.content_booking, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(list[position])
    }




    fun showDialogPilihan(
        paket: String,
        title: String,
        jenis: BookingModel,
        mode: String,
        context: Context,
        jamMode: String,
        db: DocumentReference
    ) {

        Log.d("haha", "$paket ${jenis.nama} $mode $jamMode")
        var tagihan = ""
        val appPreferences = AppPreferences(context)
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_single_checkout)
        FirebaseFirestore.getInstance().collection("harga")
            .document("instagram")
            .collection("instagram")
            .document("$mode")
            .addSnapshotListener { value, error ->
                if(jenis.prefix == "stories"){
                    dialog.total.text = "${value!!["${jenis.prefix}"]}"
                    tagihan = "${value!!["${jenis.prefix}"]}"
                }else{
                    dialog.total.text = "${value!!["$jamMode.${jenis.prefix}"]}"
                    tagihan = "${value!!["$jamMode.${jenis.prefix}"]}"
                }
            }


        dialog.checkout.setOnClickListener {
            if(true){

                val date = Date()
                date.hours = date.hours +1
                db.update("$jam.instagram.${jenis.prefix}.timestamp", date.time)
                db.update("$jam.instagram.${jenis.prefix}.isBooked", true)
                db.update("$jam.instagram.${jenis.prefix}.uid", appPreferences.uid)
                Toast.makeText(
                    context,
                    "Slot Ini Berhasil Di Booking",
                    Toast.LENGTH_SHORT
                ).show()
                val gen = TransNumGenerator(15, ThreadLocalRandom.current())
                FirebaseFirestore.getInstance().collection("histori-transaksi").add(
                    HistoriData(
                        gen.nextString(),
                        "${date.time}",
                        true,
                        false,
                        "${appPreferences.uid}",
                        "${appPreferences.nama}",
                        "${appPreferences.email}",
                        "${appPreferences.nohp}",
                        ""+appPreferences.jobs,
                        "Instagram ${jenis.nama} -$mode-",
                        null,
                        tagihan,
                        true,
                        db.id,
                        "$jam.instagram.${jenis.prefix}"
                    )
                )
                    .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                        override fun onComplete(p0: Task<DocumentReference>) {
                            p0.result?.id?.let { it1 -> Log.d("fuck", it1) }
                            val intent = Intent(context, DetailCheckout::class.java)
                            intent.putExtra("cek", "${p0.result?.id}")
                            Log.d("testIntent", "${p0.result?.id}")
                            context.startActivity(intent)
                            (context as Activity).finish()
                        }

                    })
            }
        }
        dialog.show()
    }

}