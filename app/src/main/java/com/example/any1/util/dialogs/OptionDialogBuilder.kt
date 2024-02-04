package com.example.any1.util.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.example.any1.R
import com.example.any1.util.dialogs.helper.DialogNoClickListener
import com.example.any1.util.dialogs.helper.DialogYesClickListener

object OptionDialogBuilder {
    private var dialog : Dialog ?= null
    fun createCustomDialog(activity: Activity,
                           @LayoutRes layoutFileName : Int,
                           yesResourceId: Int,
                           noResourceId : Int,
                           headerId : Int,
                           infoTextId : Int,
                           headerText : String,
                           infoText : String,
                           yesText : String,
                           noText : String,
                           dialogYesClickListener: DialogYesClickListener,
                           dialogNoClickListener: DialogNoClickListener
    ) {
        dialog = Dialog(activity.applicationContext)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(layoutFileName)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yes = dialog?.findViewById<TextView>(yesResourceId)
        val no = dialog?.findViewById<TextView>(noResourceId)
        val headerTextRes = dialog?.findViewById<TextView>(headerId)
        val infoTextRes = dialog?.findViewById<TextView>(infoTextId)
        yes?.text = yesText
        headerTextRes?.text = headerText
        infoTextRes?.text = infoText
        no?.text = noText
        yes?.setOnClickListener {
            dialogYesClickListener.onYesClick(activity)
        }
        no?.setOnClickListener {
            dialogNoClickListener.onNoClick()
        }
    }

    fun createQuickDialog(activity: Activity,
                          dialogYesClickListener: DialogYesClickListener,
                          dialogNoClickListener: DialogNoClickListener,
                          headerText: String,
                          infoText: String,
                          yesText: String,
                          noText: String
    ) {
        dialog = Dialog(activity.applicationContext)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.saveinfo)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val yes = dialog?.findViewById<TextView>(R.id.saveinfo)
        val no = dialog?.findViewById<TextView>(R.id.notnow)
        val headerId = dialog?.findViewById<TextView>(R.id.savelogininfotext)
        val infoTextId = dialog?.findViewById<TextView>(R.id.removeaccountinfo)
        headerId?.text = headerText
        yes?.text = yesText
        no?.text = noText
        infoTextId?.text = infoText
        yes?.setOnClickListener {
            dialogYesClickListener.onYesClick(activity)
        }
        no?.setOnClickListener {
            dialogNoClickListener.onNoClick()
        }
    }

    fun dismissDialog(){
        if(dialog !=null) dialog?.dismiss()
    }
}