package com.example.any1.feature_group.domain.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.R
import kotlin.collections.ArrayList


class CreateGroupAdapter(arr: ArrayList<String>, context: Context) :RecyclerView.Adapter<CreateGroupAdapter.holder>(){

    var data : ArrayList<String> = arr
    var backup = ArrayList<String>()
    var context: Context? = context
    var count = 0
    var primarycount = 0
    var secondarycount = 0
    var sharedPreferences: SharedPreferences = context.getSharedPreferences(context.packageName+"tag",MODE_PRIVATE)
    val secondarytag= sharedPreferences.getString("secondarytag","").toString()
    var primarycountstr = sharedPreferences.getString("primarycount","")
    var secondarycountstr = sharedPreferences.getString("secondarycount","")
    val primarytag= sharedPreferences.getString("primarytag","").toString()
    val tertiarytag= sharedPreferences.getString("tertiarytag","").toString()
    var globalcountstr =sharedPreferences.getString("counts","")
    var globalcount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): holder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.singlegccreate,parent,false)
        return holder(view)
    }

    class holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.creategctag)
        var imageView : ImageView = itemView.findViewById(R.id.x)
    }

    override fun getItemCount(): Int {
           return data.size
    }

    private fun cleanBackup(){
        if(backup.size == 3){
            backup.removeAt(2)
            backup.removeAt(1)
            backup.removeAt(0)
        }else if (backup.size == 2){
            backup.removeAt(1)
            backup.removeAt(0)
        }
        else if(backup.size == 1) backup.removeAt(0)
    }

    private fun updateCount(){
        if(sharedPreferences.getString("counts","")!=""){
            globalcount = sharedPreferences.getString("counts","")!!.toInt()
            globalcount = globalcount.minus(1)
            if(globalcount!=0){
                sharedPreferences.edit().putString("counts",globalcount.toString()).apply()
            }else{
                sharedPreferences.edit().putString("counts","").apply()
            }
        }
    }
    override fun onBindViewHolder(holder: holder, position: Int) {
        holder.textView.text = data[position]
        if(primarycountstr !=""){
            primarycount = primarycountstr?.toInt()!!
        }
        if(secondarycountstr !=""){
            secondarycount = secondarycountstr?.toInt()!!
        }
        if(sharedPreferences.getString("counts","")!= ""){
            if(sharedPreferences.getString("counts","")?.toInt() == 1 ){
                cleanBackup()
                backup.add(0,sharedPreferences.getString("primarytag","").toString())
            }
            if(sharedPreferences.getString("counts","")?.toInt() == 2 ){
                cleanBackup()
                backup.add(0,sharedPreferences.getString("primarytag","").toString())
                backup.add(1,sharedPreferences.getString("secondarytag","").toString())
            }
            if(sharedPreferences.getString("counts","")?.toInt() == 3){
                cleanBackup()
                backup.add(0,sharedPreferences.getString("primarytag","").toString())
                backup.add(1,sharedPreferences.getString("secondarytag","").toString())
                backup.add(2,sharedPreferences.getString("tertiarytag","").toString())

            }
        }
        holder.imageView.setOnClickListener {
            when (position) {
                0 -> {
                    primarycount ++
                    when(primarycount){
                        1->  {
                            if(backup.size == 1){
                                sharedPreferences.edit().putString("primarytag","").apply()
                                backup.removeAt(0)
                            }
                            if(backup.size == 2){
                                Log.d("tug",backup[0])
                                Log.d("tug",backup[1])
                                sharedPreferences.edit().putString("primarytag",backup[1]).apply()
                                sharedPreferences.edit().putString("secondarytag","").apply()
                                backup.removeAt(0)
                                Log.d("tug",backup[0])
                            }
                            if(backup.size ==3 ){
                                sharedPreferences.edit().putString("primarytag",backup[1]).apply()
                                sharedPreferences.edit().putString("secondarytag",backup[2]).apply()
                                sharedPreferences.edit().putString("tertiarytag","").apply()
                                backup.removeAt(0)
                            }
                            updateCount()

                        }
                        2->{
                            Log.d("tug", backup.size.toString())
                            if(backup.size == 1){
                                sharedPreferences.edit().putString("primarytag","").apply()
                                backup.removeAt(0)
                            }
                            if(backup.size == 2){
                                sharedPreferences.edit().putString("primarytag",backup[1]).apply()
                                sharedPreferences.edit().putString("secondarytag","").apply()
                                backup.removeAt(0)
                            }
                            updateCount()
                        }
                        3->{
                            sharedPreferences.edit().putString("primarytag","").apply()
                            backup.removeAt(0)
                            updateCount()
                        }
                    }
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,data.size)
                }
                1 -> {
                    secondarycount++
                    when(secondarycount){
                        1-> {
                            if(backup.size==3){
                                sharedPreferences.edit().putString("secondarytag",backup[2]).apply()
                                sharedPreferences.edit().putString("tertiarytag","").apply()
                                backup.removeAt(1)
                                Log.d("tug",backup[0])
                                Log.d("tug",backup[1])
                            }else if(backup.size == 2){
                                sharedPreferences.edit().putString("secondarytag","").apply()
                                backup.removeAt(1)
                            }
                            updateCount()
                        }
                        2->{
                            sharedPreferences.edit().putString("secondarytag","").apply()
                            backup.removeAt(1)
                            updateCount()
                        }
                    }
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,data.size)
                }
                2 -> {
                    sharedPreferences.edit().putString("tertiarytag","").apply()
                    backup.removeAt(2)
                    updateCount()
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,data.size)
                }
            }

        }

    }
}
