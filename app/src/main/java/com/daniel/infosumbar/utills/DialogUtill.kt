package com.daniel.infosumbar.utills

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import com.daniel.infosumbar.R
import com.daniel.infosumbar.`interface`.DialogListener
import kotlinx.android.synthetic.main.dialog_login_info.*


fun View.cancel(dialog: Dialog){
    this.setOnClickListener {
        dialog.cancel()
    }
}