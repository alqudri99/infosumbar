package com.alqudri.infosumbar.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alqudri.infosumbar.ui.admin.BookingInfoActivity
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.ui.DetailCheckout
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.content_history.view.*

class HistoryAdapter(var documentSnapshot: List<DocumentSnapshot>, var mode: Int) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>(){
    inner class ViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        fun bind(doc: DocumentSnapshot){
            itemView.textView2.text = "${doc.get("nama")}"
            itemView.textView3.text = "${doc.get("jenis_iklan")}"
            itemView.textView4.text = "${doc.get("no_hp")}"
            itemView.textView5.text = "${doc.get("email")}"
            itemView.gas.setOnClickListener {
                var intent = Intent()
                if(mode==1){
                    intent = Intent(itemView.context, BookingInfoActivity::class.java)
                }else{
                    intent = Intent(itemView.context, DetailCheckout::class.java)
                }
                intent.putExtra("cek", doc.id)
                itemView.context.startActivity(intent)
            }
            val isConfirm = doc.get("isConfirm") as Boolean
            val isBooked = doc.get("isBooked") as Boolean

            Log.d("booleaan", "$isBooked $isConfirm p${doc.get("bukti_pembayaran")}p")

            if (isConfirm){
                Glide.with(itemView).load(ColorDrawable(Color.parseColor("#00C853"))).into(itemView.indicator)
            }else if (isBooked && doc.get("bukti_pembayaran") == null){
                Glide.with(itemView).load(ColorDrawable(Color.parseColor("#DD2C00"))).into(itemView.indicator)
            }else if(isBooked && doc.get("bukti_pembayaran") != null){
                Glide.with(itemView).load(ColorDrawable(Color.parseColor("#FFAB00"))).into(itemView.indicator)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_history, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return documentSnapshot.size;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documentSnapshot[position])
    }

}