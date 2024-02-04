package com.example.any1.feature_tags

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.R
import java.util.*


class TagsAdapter(arr: ArrayList<Model>, context: Context, val recyclerView: RecyclerView, val string: String) :RecyclerView.Adapter<TagsAdapter.holder>() {

    var data: ArrayList<Model> = arr
    var backup = ArrayList<Model>()
    var checkedlist= ArrayList<Model>()
    var context: Context? = context
    private val limit = 20
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName+"tag", MODE_PRIVATE)
    var count = 0
    val primarytag = sharedPreferences.getString("primarytag","").toString()
    val secondarytag = sharedPreferences.getString("secondarytag","").toString()
    val tertiarytag = sharedPreferences.getString("tertiarytag","").toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.tagsinglerow, parent, false)
        return holder(view)
    }

    inner class holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var checkbox: AppCompatCheckBox = itemView.findViewById(R.id.tagcheckbox)
    }

    private fun swapItem(fromPosition: Int, toPosition: Int) {
        recyclerView.post {
            (context as Activity).runOnUiThread {
                val value = data[fromPosition]
                if (data.size != 1) {
                    data.removeAt(fromPosition)
                    data.add(toPosition, value)
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return if (data.size > limit) {
            limit
        } else {
            data.size
        }
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: holder, @SuppressLint("RecyclerView") position: Int) {
        if(string == "cg"){
            val prefcount = sharedPreferences.getString("counts","").toString()
            if(prefcount!=""){
                count = prefcount.toInt()
                if(count==1)checkedlist.add(Model(primarytag,true,true))
                if(count==2){
                    checkedlist.add(Model(primarytag,true,true))
                    checkedlist.add(Model(secondarytag,true,true))
                }
                if(count==3){
                    checkedlist.add(Model(primarytag,true,true))
                    checkedlist.add(Model(secondarytag,true,true))
                    checkedlist.add(Model(tertiarytag,true,true))
                }
            }

            if(count == 3){
                data[position].isEnabled = data[position].isSelected
                if (!data[position].isEnabled ) {
                    holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_greyplus)
                    holder.checkbox.isEnabled = false
                } else {
                    holder.checkbox.isEnabled = true
                    holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_checktick)
                }
            }
            if(count == 2) holder.checkbox.isEnabled = true

            holder.checkbox.text = data[position].item
            if(data[position].isSelected) holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!,R.drawable.ic_checktick)
            else holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!,R.drawable.ic_addsc)
            if (count <= 3) {
                holder.checkbox.setOnClickListener {
                    if(data[position].isSelected){
                        count--
                        data[position].isSelected = false
                        holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_addsc)
                        sharedPreferences.edit().putString("counts", count.toString()).apply()
                        when (count) {
                            0 -> {
                                checkedlist.remove(data[position])
                                sharedPreferences.edit().putString("primarytag", "").apply()
                                data[position].isSelected = false
                                notifyDataSetChanged()
                            }
                            1 -> {
                                if(data[position].item == checkedlist[0].item) {
                                    checkedlist.removeAt(0)
                                    sharedPreferences.edit().putString("primarytag",checkedlist[0].item).apply()
                                    sharedPreferences.edit().putString("secondarytag","").apply()
                                    swapItem(0,1)
                                    swapItem(1,2)
                                }
                                if(data[position].item == checkedlist[1].item)  {
                                    checkedlist.removeAt(1)
                                    sharedPreferences.edit().putString("secondarytag", "").apply()
                                    swapItem(1,2)
                                }
                                data[position].isSelected = false
                                notifyDataSetChanged()
                            }
                            2 -> {
                                if(data[position].item == checkedlist[0].item) {
                                    checkedlist.removeAt(0)
                                    sharedPreferences.edit().putString("primarytag",checkedlist[0].item).apply()
                                    sharedPreferences.edit().putString("secondarytag",checkedlist[1].item).apply()
                                    sharedPreferences.edit().putString("tertiarytag","").apply()
                                    swapItem(0,1)
                                    swapItem(1,2)
                                }
                                if(data[position].item == checkedlist[1].item)  {
                                    checkedlist.removeAt(1)
                                    sharedPreferences.edit().putString("primarytag",checkedlist[0].item).apply()
                                    sharedPreferences.edit().putString("secondarytag",checkedlist[1].item).apply()
                                    sharedPreferences.edit().putString("tertiarytag","").apply()
                                    swapItem(1,2)
                                }
                                if(data[position].item == checkedlist[2].item)  {
                                    checkedlist.removeAt(2)
                                    sharedPreferences.edit().putString("tertiarytag","").apply()
                                }
                                Log.d("index",checkedlist[0].item)
                                Log.d("index",checkedlist[1].item)

                                data[position].isSelected = false
                                notifyDataSetChanged()
                            }
                        }
                    }else{
                        count++
                        data[position].isSelected = true
                        sharedPreferences.edit().putString("counts", count.toString()).apply()
                        when (count) {
                            1 -> {
                                sharedPreferences.edit().putString("primarytag",data[position].item).apply()
                                swapItem(position,0)
                                checkedlist.add(0,data[position])
                                holder.checkbox.isChecked = true
                                data[position].isEnabled = true
                                data[position].isSelected = true
                            }
                            2 -> {
                                sharedPreferences.edit().putString("secondarytag",data[position].item).apply()
                                swapItem(position,1)
                                checkedlist.add(1,data[position])
                                data[position].isEnabled = true
                                holder.checkbox.isChecked = true
                                data[position].isSelected = true
                            }
                            3 -> {
                                sharedPreferences.edit().putString("tertiarytag",data[position].item).apply()
                                swapItem(position,2)
                                checkedlist.add(2,data[position])
                                holder.checkbox.isChecked = true
                                data[position].isEnabled = true
                                data[position].isSelected = true
                            }
                        }
                    }
                }
            }
        }else{
            val prefcount = sharedPreferences.getString("counts","").toString()
            if(prefcount!= ""){
                count = prefcount.toInt()
                if(count==1)checkedlist.add(Model(primarytag,true,true))
                if(count==2){
                    checkedlist.add(Model(primarytag,true,true))
                    checkedlist.add(Model(secondarytag,true,true))
                }
                if(count==3){
                    checkedlist.add(Model(primarytag,true,true))
                    checkedlist.add(Model(secondarytag,true,true))
                    checkedlist.add(Model(tertiarytag,true,true))
                }
            }
//            if(checkedlist.isNotEmpty()) {
//                sharedPreferences.edit().clear().apply()
//                if (checkedlist.size == 1) sharedPreferences.edit()
//                    .putString("primarytag", checkedlist[0].item).apply()
//                if (checkedlist.size == 2) {
//                    sharedPreferences.edit().putString("primarytag", checkedlist[0].item).apply()
//                    sharedPreferences.edit().putString("secondarytag", checkedlist[1].item).apply()
//                }
//                if (checkedlist.size == 3) {
//                    sharedPreferences.edit().putString("primarytag", checkedlist[0].item).apply()
//                    sharedPreferences.edit().putString("secondarytag", checkedlist[1].item).apply()
//                    sharedPreferences.edit().putString("tertiarytag", checkedlist[2].item).apply()
//                }
//            }
            if(count == 3){
                data[position].isEnabled = data[position].isSelected
                if (!data[position].isEnabled ) {
                    holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_greyplus)
                    holder.checkbox.isEnabled = false
                } else {
                    holder.checkbox.isEnabled = true
                    holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!, R.drawable.ic_checktick)
                }
            }
            if(count == 2) holder.checkbox.isEnabled = true

            holder.checkbox.text = data[position].item
            if(data[position].isSelected) holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!,R.drawable.ic_checktick)
            else holder.checkbox.buttonDrawable = AppCompatResources.getDrawable(context!!,R.drawable.ic_addsc)
            if(count == 1){
                holder.checkbox.setOnClickListener {
                    if (data[position].isSelected){
                        val dialog = Dialog(context!!)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(R.layout.tagremovedialog)
                        val ok = dialog.findViewById<TextView>(R.id.ok)
                        ok.setOnClickListener { dialog.dismiss() }
                        dialog.show()
                    }else{
                        count++
                        sharedPreferences.edit().putString("counts", count.toString()).apply()
                        sharedPreferences.edit().clear().apply()
                        sharedPreferences.edit().putString("primarytag",checkedlist[0].item).apply()
                        sharedPreferences.edit()
                            .putString("secondarytag", data[position].item).apply()
                        swapItem(position, 0)
                        checkedlist.add(1, data[position])
                        holder.checkbox.isChecked = true
                        data[position].isEnabled = true
                        data[position].isSelected = true
                    }
                }
            }
            if (count == 2 || count == 3) {
                holder.checkbox.setOnClickListener {
                    if (data[position].isSelected) {
                        count--
                        data[position].isSelected = false
                        holder.checkbox.buttonDrawable =
                            AppCompatResources.getDrawable(context!!, R.drawable.ic_addsc)
                        sharedPreferences.edit().putString("counts", count.toString()).apply()
                        when (count){
                            2->{
                                if (data[position].item == checkedlist[0].item) {
                                    checkedlist.removeAt(0)
                                    sharedPreferences.edit()
                                        .putString("primarytag", checkedlist[0].item).apply()
                                    sharedPreferences.edit()
                                        .putString("secondarytag", checkedlist[1].item).apply()
                                    sharedPreferences.edit().putString("tertiarytag", "").apply()
                                    swapItem(0, 1)
                                    swapItem(1, 2)
                                }
                                if (data[position].item == checkedlist[1].item) {
                                    checkedlist.removeAt(1)
                                    sharedPreferences.edit()
                                        .putString("primarytag", checkedlist[0].item).apply()
                                    sharedPreferences.edit()
                                        .putString("secondarytag", checkedlist[1].item).apply()
                                    sharedPreferences.edit().putString("tertiarytag", "").apply()
                                    swapItem(1, 2)
                                }
                                if (data[position].item == checkedlist[2].item) {
                                    checkedlist.removeAt(2)
                                    sharedPreferences.edit().putString("tertiarytag", "").apply()
                                }
                                Log.d("index", checkedlist[0].item)
                                Log.d("index", checkedlist[1].item)

                                data[position].isSelected = false
                                notifyDataSetChanged()

                            }
                            1 ->{
                                if (data[position].item == checkedlist[0].item) {
                                    checkedlist.removeAt(0)
                                    sharedPreferences.edit()
                                        .putString("primarytag", checkedlist[1].item).apply()
                                    sharedPreferences.edit()
                                        .putString("secondarytag", "").apply()
                                    sharedPreferences.edit().putString("tertiarytag", "").apply()
                                    swapItem(0, 1)
                                    swapItem(1, 2)
                                }
                                if(data[position].item == checkedlist[1].item) {
                                    checkedlist.removeAt(1)
                                    sharedPreferences.edit()
                                        .putString("primarytag", checkedlist[0].item).apply()
                                    sharedPreferences.edit()
                                        .putString("secondarytag", "").apply()
                                    sharedPreferences.edit().putString("tertiarytag", "").apply()
                                }
                            }
                        }
                    }else {
                        count++
                        data[position].isSelected = true
                        sharedPreferences.edit().putString("counts", count.toString()).apply()
                        when (count) {
                            2 -> {
                                sharedPreferences.edit()
                                    .putString("secondarytag", data[position].item).apply()
                                swapItem(position, 1)
                                checkedlist.add(1, data[position])
                                data[position].isEnabled = true
                                holder.checkbox.isChecked = true
                                data[position].isSelected = true
                            }
                            3 -> {
                                sharedPreferences.edit()
                                    .putString("tertiarytag", data[position].item).apply()
                                swapItem(position, 2)
                                checkedlist.add(2, data[position])
                                holder.checkbox.isChecked = true
                                data[position].isEnabled = true
                                data[position].isSelected = true
                            }
                        }
                    }
                }
            }
        }
    }
}
