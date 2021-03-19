package com.alqudri.infosumbar.utills

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.alqudri.infosumbar.AnimationView
import com.alqudri.infosumbar.R
import com.alqudri.infosumbar.model.HargaModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.include_harga.view.*
import kotlinx.android.synthetic.main.include_paket.view.*
import kotlinx.android.synthetic.main.include_single_ig.view.*
import kotlinx.android.synthetic.main.include_single_ig.view.jenis_pengguna

fun List<Button>.disableButton() {
    for (view: Button in this) {
        view.setBackgroundResource(R.drawable.bg_empty)
        view.setTextColor(Color.parseColor("#DCD9D9"))
        view.isClickable = false
    }
}

fun List<View>.hideViews(state: Boolean) {
    for (view: View in this) {
        if (state) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}

fun View.hideView(state: Boolean) {
    if (state) {
        this.visibility = View.GONE
    } else {
        this.visibility = View.VISIBLE
    }
}

fun List<Button>.colorState(placeholder: View, pilihan: View, activeButton: Button) {
    val animation = AnimationView()
    activeButton.setBackgroundResource(R.drawable.bg_button_tanggal2)
    animation.init(placeholder)
    animation.showOut(pilihan)
    for (button: Button in this) {
        button.setBackgroundResource(R.drawable.bg_button_tanggal1)
    }
}

fun List<Button>.colorActive(buttonState: Boolean) {
    if (buttonState) {
        this.get(0).setBackgroundResource(R.drawable.bg_proses)
        this.get(1).setBackgroundResource(R.drawable.bg_terima_disable)
    } else {
        this.get(0).setBackgroundResource(R.drawable.bg_proses_disable)
        this.get(1).setBackgroundResource(R.drawable.bg_button_tanggal22)
    }

}


fun List<LinearLayout>.layoutBind(
    value: DocumentSnapshot,
    data: List<String>,
    title: String,
    jam: List<String>
) {
    var i = 0;
    for (layBind: LinearLayout in this) {
        layBind.jenis_pengguna_umkm.text = title
        layBind.jadwal.text = jam[i]
        layBind.edt_igtv_umkm.setText("${value["${data[i]}.igtv"]}")
        layBind.edt_feed_umkm.setText("${value["${data[i]}.feed"]}")
        layBind.edt_stories_umkm.setText("${value["stories"]}")
        i++
    }
}

fun List<LinearLayout>.layoutBindUmum(
    value: DocumentSnapshot,
    data: List<String>,
    title: String,
    jam: List<String>
) {
    var i = 0;
    for (layBind: LinearLayout in this) {
        layBind.jenis_pengguna.text = title
        layBind.jadwal.text = jam[i]
        layBind.edt_igtv.setText("${value["${data[i]}.igtv"]}")
        layBind.edt_feed.setText("${value["${data[i]}.feed"]}")
        layBind.edt_stories.setText("${value["stories"]}")
        i++
    }
}

fun List<LinearLayout>.saveData(
    db: FirebaseFirestore,
    hargaUmkm: LinearLayout,
    paketUmkm: LinearLayout
) {
    val jam9: HashMap<String, String> = HashMap()
    val jam13: HashMap<String, String> = HashMap()
    val jam16: HashMap<String, String> = HashMap()
    val jam19: HashMap<String, String> = HashMap()
    val jam22: HashMap<String, String> = HashMap()
    val facebook: HashMap<String, String> = HashMap()
    var i = 0
    val jadwal = arrayListOf<HashMap<String, String>>(jam9, jam13, jam16, jam19, jam22)
    for (lay: LinearLayout in this) {
        jadwal[i].put("feed", lay.edt_feed_umkm.text.toString())
        jadwal[i].put("igtv", lay.edt_igtv_umkm.text.toString())
        i++
    }

    facebook.put("foto", hargaUmkm.edt_fb_foto.makeString())
    facebook.put("video", hargaUmkm.edt_fb_video.makeString())
    val data =
        HargaModel(
            hargaUmkm.edt_twitter.makeString(),
            this[0].edt_stories_umkm.makeString(),
            paketUmkm.edt_paket1.makeString(),
            paketUmkm.edt_paket2.makeString(),
            paketUmkm.edt_paket3.makeString(),
            jam9, jam13, jam16, jam19, jam22
        )

    db.collection("harga")
        .document("instagram")
        .collection("instagram")
        .document("umkm").set(data)
}


fun List<LinearLayout>.saveDataUmum(
    db: FirebaseFirestore,
    hargaUmkm: LinearLayout,
    paketUmkm: LinearLayout
) {
    val jam9: HashMap<String, String> = HashMap()
    val jam13: HashMap<String, String> = HashMap()
    val jam16: HashMap<String, String> = HashMap()
    val jam19: HashMap<String, String> = HashMap()
    val jam22: HashMap<String, String> = HashMap()
    val facebook: HashMap<String, String> = HashMap()
    var i = 0
    val jadwal = arrayListOf<HashMap<String, String>>(jam9, jam13, jam16, jam19, jam22)
    for (lay: LinearLayout in this) {
        jadwal[i].put("feed", lay.edt_feed.text.toString())
        jadwal[i].put("igtv", lay.edt_igtv.text.toString())
        i++
    }

    facebook.put("foto", hargaUmkm.edt_fb_foto.makeString())
    facebook.put("video", hargaUmkm.edt_fb_video.makeString())
    val data =
        HargaModel(
            hargaUmkm.edt_twitter.makeString(),
            this[0].edt_stories.makeString(),
            paketUmkm.edt_paket1.makeString(),
            paketUmkm.edt_paket2.makeString(),
            paketUmkm.edt_paket3.makeString(),
            jam9, jam13, jam16, jam19, jam22
        )

    db.collection("harga")
        .document("instagram")
        .collection("instagram")
        .document("umum").set(data)
}


fun EditText.makeString(): String {
    return this.text.toString()
}

fun ImageView.loadRounded(url: Any?, transform: (() -> Transformation<Bitmap>?)? = null) {
    val glide = Glide.with(this.context)
        .load(url)
    transform?.let {
        glide.transform(CenterCrop(), it.invoke())
    }
    glide.into(this)
}