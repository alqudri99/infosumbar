package com.daniel.infosumbar

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.tejpratapsingh.pdfcreator.activity.PDFCreatorActivity
import com.tejpratapsingh.pdfcreator.utils.PDFUtil.PDFUtilListener
import com.tejpratapsingh.pdfcreator.views.PDFBody
import com.tejpratapsingh.pdfcreator.views.PDFHeaderView
import com.tejpratapsingh.pdfcreator.views.PDFTableView
import com.tejpratapsingh.pdfcreator.views.PDFTableView.PDFTableRowView
import com.tejpratapsingh.pdfcreator.views.basic.PDFImageView
import com.tejpratapsingh.pdfcreator.views.basic.PDFLineSeparatorView
import com.tejpratapsingh.pdfcreator.views.basic.PDFTextView
import java.io.File
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PdfActivity : PDFCreatorActivity() {
    val db = FirebaseFirestore.getInstance().collection("histori-transaksi")
    var printData: List<DocumentSnapshot> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        db.whereEqualTo("isConfirm", true).get().addOnCompleteListener {
            if(it.isSuccessful){
                if(!(it.result?.isEmpty)!!){
                    printData = it.result?.documents!!

                    val sdf = SimpleDateFormat("dd-M-yyyy")
                    val df = Date()
                    for (doc in printData){
                        df.time = doc["timestamp"].toString().toLong()

                        val currentDate = sdf.format(df)
                        Log.d("tagging", currentDate)

                    }
                    createPDF("test", object : PDFUtilListener {
                        override fun pdfGenerationSuccess(savedPDFFile: File?) {
                            Toast.makeText(this@PdfActivity, "PDF Created", Toast.LENGTH_SHORT).show()
                        }

                        override fun pdfGenerationFailure(exception: Exception?) {
                            Toast.makeText(this@PdfActivity, "PDF NOT Created", Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }
            }
        }


    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)
        val pdfTextViewPage =
            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        pdfTextViewPage.setText(
            String.format(
                Locale.getDefault(),
                "Page: %d",
                pageIndex + 1
            )
        )
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL
        headerView.addView(pdfTextViewPage)
//        val horizontalView = PDFHorizontalView(applicationContext)
//        val pdfTextView =
//            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
//        val word = SpannableString("Transaksi")
//        word.setSpan(
//            ForegroundColorSpan(Color.DKGRAY),
//            0,
//            word.length,
//            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//        pdfTextView.text = word
//        pdfTextView.setLayout(
//            LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.MATCH_PARENT, 1f
//            )
//        )
//        pdfTextView.view.gravity = Gravity.CENTER_VERTICAL
//        pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
//        horizontalView.addView(pdfTextView)
//        val imageView = PDFImageView(applicationContext)
//        val imageLayoutParam = LinearLayout.LayoutParams(
//            200,
//            60, 0f
//        )
//        imageView.setImageScale(ImageView.ScaleType.CENTER_INSIDE)
//        imageView.setImageResource(R.drawable.logo)
//        imageLayoutParam.setMargins(0, 0, 10, 0)
//        imageView.setLayout(imageLayoutParam)
//        horizontalView.addView(imageView)
//        headerView.addView(horizontalView)
//        val lineSeparatorView1 =
//            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
//        headerView.addView(lineSeparatorView1)
        return headerView
    }

    override fun getBodyViews(): PDFBody {

        val imageView = PDFImageView(applicationContext)
        val imageLayoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            250, 0f
        )
        imageView.setImageResource(R.drawable.head)
        imageLayoutParam.setMargins(0, 0, 0, 0)
        imageView.setLayout(imageLayoutParam)


        val pdfBody = PDFBody()
        pdfBody.addView(imageView)
        val textInTable = arrayOf("No", "No Transaksi", "Nama", "Pekerjaan", "Tanggal", "Jenis Iklan", "Tagihan")
        val tableHeader = PDFTableRowView(applicationContext)
        for (s in textInTable) {
            val pdfTextView =
                PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextView.setText("$s")
            tableHeader.addToRow(pdfTextView)
        }
        val tableRowView1 = PDFTableRowView(applicationContext)
        val tableView =
            PDFTableView(applicationContext, tableHeader, tableRowView1)
        var no = 1
        for (doc: DocumentSnapshot in printData) {
            // Create 10 rows
            val tableRowView = PDFTableRowView(applicationContext)
            val gDate = Date()
            val list = arrayListOf("$no", "no_transaksi", "nama", "jenis_pekerjaan", "timestamp", "jenis_iklan", "tagihan")
            var j = 0
            for (s in list) {
                if(j == 0){
                    val pdfTextView =
                        PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                    pdfTextView.setText("$no")
                    tableRowView.addToRow(pdfTextView)
                }else if (j == 4){
                    val pdfTextView =
                        PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                    gDate.time = doc[s].toString().toLong()
                    val sdf = SimpleDateFormat("dd-M-yyyy")
                    val t = Date()
                    val currentDate = sdf.format(t)
                    pdfTextView.setText("$currentDate")
                    tableRowView.addToRow(pdfTextView)
                }else{
                    val pdfTextView =
                        PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                    pdfTextView.setText("${doc[s]}")
                    tableRowView.addToRow(pdfTextView)
                }
                j++
            }
            tableView.addRow(tableRowView)

            no++
        }
        pdfBody.addView(tableView)
        val lineSeparatorView3 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.BLACK)
        pdfBody.addView(lineSeparatorView3)
        val pdfIconLicenseView =
            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        val icon8Link =
            Html.fromHtml("Website <a href='https://infosumbar.com'>InfoSumbar.com</a>")
        pdfIconLicenseView.view.setText(icon8Link)
        pdfBody.addView(pdfIconLicenseView)


        return pdfBody
    }

    override fun onNextClicked(savedPDFFile: File) {
        val pdfUri = Uri.fromFile(savedPDFFile)
        val intentPdfViewer = Intent(this@PdfActivity, PdfViewerActivity::class.java)
//        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri)
        startActivity(intentPdfViewer)
    }
}