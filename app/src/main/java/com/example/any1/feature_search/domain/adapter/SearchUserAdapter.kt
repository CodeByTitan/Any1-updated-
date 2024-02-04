package com.example.any1.feature_search.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_search.domain.model.SearchUserModel
import com.bumptech.glide.Glide
import com.example.any1.R
import com.google.android.material.button.MaterialButton

class SearchUserAdapter (val context : Context) : RecyclerView.Adapter<SearchUserAdapter.SearchUserHolder>(){

    inner class SearchUserHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val displayname : TextView = itemView.findViewById(R.id.userdisplayname)
        val username : TextView = itemView.findViewById(R.id.username)
        val usersearchpic : ImageView = itemView.findViewById(R.id.usersearchpic)
        val connect: MaterialButton = itemView.findViewById(R.id.connect)
        val connected : TextView = itemView.findViewById(R.id.connected)
        val requested : TextView = itemView.findViewById(R.id.requested)
    }
    private lateinit var arrayList : ArrayList<SearchUserModel>
    fun setAdapterList(arrayList: ArrayList<SearchUserModel>){
        this.arrayList = arrayList
    }

    fun clearUsersList(){
        arrayList.clear()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchUserHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.searchusersinglerow,parent,false)
        return SearchUserHolder(view)
    }

    override fun onBindViewHolder(holder: SearchUserHolder, position: Int) {
        holder.displayname.text = arrayList[position].displayname
        holder.username.text = arrayList[position].username
        if(arrayList[position].imageuri != "male" || arrayList[position].imageuri !="female")Glide.with(context).load(arrayList[position].imageuri).circleCrop().into(holder.usersearchpic)
        else if(arrayList[position].imageuri == "male")holder.usersearchpic.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.gigachad))
        else{
            holder.usersearchpic.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.doomergirl))
        }
        val isConnected = arrayList[position].connected
        val isRequested = arrayList[position].requested
        if(isConnected && !isRequested){
            holder.connected.visibility = View.VISIBLE
            holder.connect.visibility = View.INVISIBLE
            holder.requested.visibility = View.INVISIBLE
        }else if(!isConnected && isRequested){
            holder.connected.visibility = View.INVISIBLE
            holder.connect.visibility = View.INVISIBLE
            holder.requested.visibility = View.VISIBLE
        }else if(!isConnected && !isRequested){
            holder.connected.visibility = View.INVISIBLE
            holder.connect.visibility = View.VISIBLE
            holder.requested.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}