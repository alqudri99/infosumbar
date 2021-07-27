package com.daniel.infosumbar.utills

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.daniel.infosumbar.AnimationView
import com.daniel.infosumbar.R
import com.daniel.infosumbar.model.HargaModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.daniel.infosumbar.model.userData
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.include_harga.view.*
import kotlinx.android.synthetic.main.include_paket.view.*
import kotlinx.android.synthetic.main.include_single_ig.view.*
import kotlinx.android.synthetic.main.include_single_ig.view.jenis_pengguna
import java.util.concurrent.ThreadLocalRandom

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
        layBind.edt_stories_umkm.setText("${value["${data[i]}.stories"]}")
        layBind.edt_highlight_umkm.setText("${value["${data[i]}.highlight"]}")
        layBind.edt_stories_visit_umkm.setText("${value["${data[i]}.instastory"]}")
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
        layBind.edt_stories.setText("${value["${data[i]}.stories"]}")
        layBind.edt_highlight.setText("${value["${data[i]}.highlight"]}")
        layBind.edt_stories_visit.setText("${value["${data[i]}.instastory"]}")
        i++
    }
}

interface SaveDataListener{
    fun saveData(state: Boolean)
}
fun List<LinearLayout>.saveData(
    db: FirebaseFirestore,
    hargaUmkm: LinearLayout,
    paketUmkm: LinearLayout, saveDataListener: SaveDataListener
) {
    val jam9: HashMap<String, String> = HashMap()
    val jam13: HashMap<String, String> = HashMap()
    val jam16: HashMap<String, String> = HashMap()
    val jam19: HashMap<String, String> = HashMap()
    val jam22: HashMap<String, String> = HashMap()
    val facebook: HashMap<String, String> = HashMap()
    val twitter: HashMap<String, String> = HashMap()
    var i = 0
    val jadwal = arrayListOf<HashMap<String, String>>(jam9, jam13, jam16, jam19, jam22)
    for (lay: LinearLayout in this) {
        jadwal[i].put("feed", lay.edt_feed_umkm.text.toString())
        jadwal[i].put("igtv", lay.edt_igtv_umkm.text.toString())
        jadwal[i].put("stories", lay.edt_stories_umkm.text.toString())
        jadwal[i].put("highlight", lay.edt_highlight_umkm.text.toString())
        jadwal[i].put("instastory", lay.edt_stories_visit_umkm.text.toString())
        i++
    }

    facebook.put("foto", hargaUmkm.edt_fb_foto.makeString())
    facebook.put("video", hargaUmkm.edt_fb_video.makeString())

    twitter.put("tweet", hargaUmkm.edt_twitter_tweet.makeString())
    twitter.put("fleet", hargaUmkm.edt_twitter_fleet.makeString())

    val data =
        HargaModel(
            paketUmkm.edt_paket1.makeString(),
            paketUmkm.edt_paket2.makeString(),
            paketUmkm.edt_paket3.makeString(),
            jam9, jam13, jam16, jam19, jam22,
            facebook, twitter
        )

    db.collection("harga")
        .document("instagram")
        .collection("instagram")
        .document("umkm").set(data).addOnCompleteListener {
            saveDataListener.saveData(it.isSuccessful)
        }
}


fun List<LinearLayout>.saveDataUmum(
    db: FirebaseFirestore,
    hargaUmkm: LinearLayout,
    paketUmkm: LinearLayout, saveDataListener: SaveDataListener
) {
    val jam9: HashMap<String, String> = HashMap()
    val jam13: HashMap<String, String> = HashMap()
    val jam16: HashMap<String, String> = HashMap()
    val jam19: HashMap<String, String> = HashMap()
    val jam22: HashMap<String, String> = HashMap()
    val facebook: HashMap<String, String> = HashMap()
    val twitter: HashMap<String, String> = HashMap()
    var i = 0
    val jadwal = arrayListOf<HashMap<String, String>>(jam9, jam13, jam16, jam19, jam22)
    for (lay: LinearLayout in this) {
        jadwal[i].put("feed", lay.edt_feed.text.toString())
        jadwal[i].put("igtv", lay.edt_igtv.text.toString())
        jadwal[i].put("stories", lay.edt_stories.text.toString())
        jadwal[i].put("highlight", lay.edt_highlight.text.toString())
        jadwal[i].put("instastory", lay.edt_stories_visit.text.toString())
        i++
    }

    facebook.put("foto", hargaUmkm.edt_fb_foto.makeString())
    facebook.put("video", hargaUmkm.edt_fb_video.makeString())

    twitter.put("tweet", hargaUmkm.edt_twitter_tweet.makeString())
    twitter.put("fleet", hargaUmkm.edt_twitter_fleet.makeString())
    val data =
        HargaModel(
            paketUmkm.edt_paket1.makeString(),
            paketUmkm.edt_paket2.makeString(),
            paketUmkm.edt_paket3.makeString(),
            jam9, jam13, jam16, jam19, jam22,
            facebook, twitter
        )

    db.collection("harga")
        .document("instagram")
        .collection("instagram")
        .document("umum").set(data).addOnCompleteListener {
            saveDataListener.saveData(it.isSuccessful)
        }
}


fun EditText.makeString(): String {
    return this.text.toString()
}

fun Any.fieldToString(fieldName: String) = this.toString()

fun View.close(dialog: Dialog){
    dialog.cancel()
}

fun QuerySnapshot.queryToString(fieldName: String): String = this.documents[0][fieldName].toString()

fun randomString(length: Int): String =
    TransNumGenerator(length, ThreadLocalRandom.current()).nextString()

fun ImageView.loadRounded(url: Any?, transform: (() -> Transformation<Bitmap>?)? = null) {
    val glide = Glide.with(this.context)
        .load(url)
    transform?.let {
        glide.transform(CenterCrop(), it.invoke())
    }
    glide.into(this)
}

fun userCheck(email: String): Boolean{
    var isItEMpty = false
    FirebaseFirestore.getInstance().collection("data-pengguna").whereEqualTo("email", email)
        .addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                val data = value!!.documents
                if (data.size == 0) isItEMpty = true else isItEMpty = false

            }

        })
    return isItEMpty
}