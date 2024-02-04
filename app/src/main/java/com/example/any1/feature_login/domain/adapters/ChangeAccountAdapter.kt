package com.example.any1.feature_login.domain.adapters

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.feature_login.domain.interfaces.AccountClickListener
import com.example.any1.feature_login.domain.model.SavedAccountModel

class ChangeAccountAdapter(val context: Context, val arrayList: ArrayList<SavedAccountModel>, val accountClickListener : AccountClickListener) : RecyclerView.Adapter<ChangeAccountAdapter.ChangeAccountHolder>(){

    inner class ChangeAccountHolder(val itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById(R.id.savedaccountimg)
        val textView : TextView = itemView.findViewById(R.id.savedaccountusername)
        val checkBox : CheckBox = itemView.findViewById(R.id.changeaccountcheckbox)
        val linearlayout : LinearLayoutCompat = itemView.findViewById(R.id.linearlayout)
        init {
            itemView.setOnClickListener{accountClickListener.onAccountClick(arrayList[adapterPosition])}
            checkBox.setOnClickListener{accountClickListener.onAccountClick(arrayList[adapterPosition])}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeAccountHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.changeaccountsinglerow,parent,false)
        return ChangeAccountHolder(view)
    }

    override fun onBindViewHolder(holder: ChangeAccountHolder, position: Int) {
        val imageurl = arrayList[position].imageurl
        val username = arrayList[position].username
        if(imageurl!="" && imageurl!="male" && imageurl!="female"){
            Glide.with(context).load(imageurl).circleCrop().into(holder.imageView)
        }else{
            if(imageurl=="male"){
                holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.gigachad))
            }else if(imageurl=="female"){
                holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.doomergirl))
            }
        }
        holder.textView.text = username
        val sharedPreferences = context.getSharedPreferences(context.packageName+"user",MODE_PRIVATE)
//        Toast.makeText(context, sharedPreferences.getString("username",""), Toast.LENGTH_SHORT).show()
        holder.checkBox.isChecked = sharedPreferences.getString("username","")==username
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}