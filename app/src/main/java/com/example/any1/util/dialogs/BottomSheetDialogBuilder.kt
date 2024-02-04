package com.example.any1.util.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.R
import com.example.any1.feature_login.domain.adapters.ChangeAccountAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheetDialogBuilder {
    private var dialog : Dialog ?=null
    fun createDialog(context: Context,
                     @LayoutRes layoutRes: Int,
                     textId1: Int,
                     textId2: Int,
                     onClickListener1: View.OnClickListener,
                     onClickListener2: View.OnClickListener
    ){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutRes)
        val text1 = dialog.findViewById<TextView>(textId1)
        val text2 = dialog.findViewById<TextView>(textId2)
        text1?.setOnClickListener {
            onClickListener1.onClick(it)
        }
        text2?.setOnClickListener {
            onClickListener2.onClick(it)
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    fun dismissDialog(){
        if(dialog !=null){
            dialog?.dismiss()
        }
    }

    fun createDialogWithImage(context: Context,
                     @LayoutRes layoutRes: Int,
                     textId1: Int,
                     imageId: Int,
                     onClickListener1: View.OnClickListener,
                     onClickListener2: View.OnClickListener
    ){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutRes)
        val text = dialog.findViewById<TextView>(textId1)
        val image = dialog.findViewById<ImageView>(imageId)
        text?.setOnClickListener {
            onClickListener1.onClick(it)
        }
        image?.setOnClickListener {
            onClickListener2.onClick(it)
        }
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    fun createDialogWithImageAndRecyclerView(context: Context,
                                             @LayoutRes layoutRes: Int,
                                             textId1: Int,
                                             imageId: Int,
                                             recyclerViewId : Int,
                                             onClickListener1: View.OnClickListener,
                                             onClickListener2: View.OnClickListener,
                                             adapter : ChangeAccountAdapter,
                                             layoutManager: LinearLayoutManager
    ){
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutRes)
        val text = dialog.findViewById<TextView>(textId1)
        val image = dialog.findViewById<ImageView>(imageId)
        text?.setOnClickListener {
            onClickListener1.onClick(it)
        }
        image?.setOnClickListener {
            onClickListener2.onClick(it)
        }
        dialog.show()

        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val recyclerView = dialog.findViewById<RecyclerView>(recyclerViewId)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = adapter
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.Dialoganimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }
}