package com.example.any1.feature_search.domain.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_search.domain.interfaces.OnSearchGroupClickListener
import com.example.any1.feature_search.domain.model.SearchModel
import com.bumptech.glide.Glide
import com.example.any1.R

class SearchAdapter(val searchGroupClickListener: OnSearchGroupClickListener, val context: Context) : RecyclerView.Adapter<SearchAdapter.SearchHolder>()  {

    private var arrayList : ArrayList<SearchModel> = ArrayList()
    inner class SearchHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val textView: TextView = itemView.findViewById(R.id.gcname)
        val imageView: ImageView = itemView.findViewById(R.id.gcsmallpic)
        val lock : ImageView = itemView.findViewById(R.id.lock)
        val membercount : TextView = itemView.findViewById(R.id.membercount)
        val groupConnections : TextView = itemView.findViewById(R.id.groupconnections)
        init {
            textView.setOnClickListener {
                searchGroupClickListener.OnGroupClicked(adapterPosition,arrayList)
            }
        }
    }

    fun clearGroups(){
        arrayList.clear()
    }

    fun setFilteredGroups(arrayList: ArrayList<SearchModel>){
        this.arrayList = arrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.searchsinglerow,parent,false)
        return SearchHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.textView.text = arrayList[position].item
        if(arrayList[position].uri == ""){
            val drawable = AppCompatResources.getDrawable(context,R.drawable.wojakgc)
            holder.imageView.setImageDrawable(drawable)
        }else{
            Glide.with(context).load(arrayList[position].uri).circleCrop().into(holder.imageView)
        }
        val isApprovalRequired: Boolean = arrayList[position].isApprovalRequired
        val membercount = arrayList[position].membercount
        holder.membercount.text = "$membercount/30"
        if(arrayList[position].connections == 0){
            holder.groupConnections.visibility = View.INVISIBLE
        }else{
            holder.groupConnections.visibility = View.VISIBLE
            holder.groupConnections.text = context.getString(R.string.groupconnections,arrayList[position].connections)
        }
        if(isApprovalRequired){
            holder.lock.visibility = View.VISIBLE
        }else{
            holder.lock.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
       return arrayList.size
    }
}