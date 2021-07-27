package com.daniel.infosumbar.ui

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.daniel.infosumbar.R

class DialogBio(context: Context) : Dialog(context) {
    override fun addContentView(view: View, params: ViewGroup.LayoutParams?) {
        super.addContentView(view, params)
        setContentView(R.layout.dialog_biodata)
    }
}