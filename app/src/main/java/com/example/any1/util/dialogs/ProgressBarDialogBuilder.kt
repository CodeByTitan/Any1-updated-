package com.example.any1.util.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.TextView
import com.example.any1.R


object ProgressBarDialogBuilder{
    private var dialog : Dialog ?= null
    fun createDialog(context : Context, text : String){
        dialog = Dialog(context)
        if(dialog !=null) {
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.progressbardialog)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val pleasewait = dialog?.findViewById<TextView>(R.id.pleasewait)
            pleasewait?.text = text
            dialog?.setCancelable(false)
            dialog?.show()
        }
    }

    fun dismissDialog(){
        if(dialog !=null) dialog?.dismiss()
    }
}