package com.example.any1.feature_group.domain.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_search.domain.model.RequestModel
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.feature_group.domain.interfaces.OnRequestClickListener
import com.example.any1.feature_group.domain.interfaces.RequestRemovedListener
import com.google.firebase.firestore.FirebaseFirestore

class RequestAdapter(val context : Context, val onRequestClickListener: OnRequestClickListener, val string: String, val requestRemovedListener: RequestRemovedListener) : RecyclerView.Adapter<RequestAdapter.RequestHolder>() {

    private val requestList = ArrayList<RequestModel>()
    private val userIdList = ArrayList<String>()

    inner class RequestHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name : TextView = itemView.findViewById(R.id.membername)
        val username : TextView = itemView.findViewById(R.id.memberusername)
        val x : ImageView = itemView.findViewById(R.id.removerequestimage)
        val checkbox : CheckBox = itemView.findViewById(R.id.requestcheckbox)
        val imageView : ImageView = itemView.findViewById(R.id.memberpfp)
        init {
            x.setOnClickListener {
                val dialog = Dialog(context)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.removerequest)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val removeRequest = dialog.findViewById<TextView>(R.id.removerequest)
                val cancel = dialog.findViewById<TextView>(R.id.requestcancel)
                val requesttext = dialog.findViewById<TextView>(R.id.removerequesttext)
                requesttext.text = context.getString(R.string.removerequest,requestList[adapterPosition].name)
                removeRequest.setOnClickListener {
                    val arrayList = ArrayList<String>()
                    for(i in requestList){
                        arrayList.add(i.id)
                    }
                    arrayList.remove(requestList[adapterPosition].id)
                    FirebaseFirestore.getInstance().collection("groups").document(string).update("requests",arrayList).addOnSuccessListener {
                        notifyDataSetChanged()
                        dialog.dismiss()
                        if(requestList.size==0){
                            requestRemovedListener.onRequestRemoved()
                        }
                    }
                }
                cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }


    fun getUserIdOfCheckedRequests(): ArrayList<String>{
        return userIdList
    }

    fun setRequestList(arrayList: List<RequestModel>){
        requestList.addAll(arrayList)
    }

    fun clearList(){
        requestList.clear()
    }
    fun notifyRemove(int: Int){
        notifyItemRemoved(int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.requestsinglerow,parent,false)
        return RequestHolder(view)
    }

    override fun onBindViewHolder(holder: RequestHolder, position: Int) {
        userIdList.clear()
        if(requestList.size == 0){
            requestRemovedListener.onRequestRemoved()
        }
        holder.name.text = requestList[position].name
        when (requestList[position].uri) {
            "male" -> {
                val drawable = AppCompatResources.getDrawable(context,R.drawable.gigachad)
                holder.imageView.setImageDrawable(drawable)
            }
            "female" -> {
                val drawable = AppCompatResources.getDrawable(context,R.drawable.doomergirl)
                holder.imageView.setImageDrawable(drawable)
            }
            else -> {
                Glide.with(context).load(requestList[position].uri).circleCrop().into(holder.imageView)
            }
        }
        holder.username.text = requestList[position].username
        holder.checkbox.isChecked = requestList[position].isChecked
        if(requestList[position].isChecked){
            if(!userIdList.contains(requestList[position].id))  userIdList.add(requestList[position].id)
        }else{
            if(userIdList.contains(requestList[position].id)){
                userIdList.remove(requestList[position].id)
            }
        }
        holder.checkbox.setOnClickListener {
            if(requestList[position].isChecked){
                requestList[position].isChecked = false
                holder.checkbox.isChecked = false
                userIdList.remove(requestList[position].id)
            }else{
                requestList[position].isChecked = true
                holder.checkbox.isChecked = true
                userIdList.add(requestList[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

}