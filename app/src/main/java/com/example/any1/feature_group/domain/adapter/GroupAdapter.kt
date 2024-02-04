package com.example.any1.feature_group.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_group.domain.model.GroupModel
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.feature_group.domain.interfaces.OnGroupClickListener
import com.mikhaellopez.circularimageview.CircularImageView

class GroupAdapter(val groupClickListener: OnGroupClickListener, val context: Context)
    : RecyclerView.Adapter<GroupAdapter.GroupHolder>() {
//    var onItemClick: ((GroupModel) -> Unit)? = null
var arrayList = ArrayList<GroupModel>()
    inner class GroupHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textView: TextView = itemView.findViewById(R.id.gcname)
        val imageView: CircularImageView = itemView.findViewById(R.id.gcsmallpic)
        init {
            textView.setOnClickListener {
               groupClickListener.OnGroupClicked(adapterPosition,arrayList)
            }
            imageView.setOnClickListener{
                groupClickListener.OnGroupClicked(adapterPosition,arrayList)
            }
        }
    }

    fun setGroupModelList(arrayList: ArrayList<GroupModel>){
        this.arrayList = arrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.groupsinglerow, parent, false)
        return GroupHolder(view)
    }

    override fun onBindViewHolder(holder: GroupHolder, position: Int) {
        holder.textView.text = arrayList[position].item
        if(arrayList[position].uri == ""){
            val drawable = AppCompatResources.getDrawable(context,R.drawable.wojakgc)
            holder.imageView.setImageDrawable(drawable)
        }else{
            Glide.with(context).load(arrayList[position].uri).circleCrop().into(holder.imageView)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}