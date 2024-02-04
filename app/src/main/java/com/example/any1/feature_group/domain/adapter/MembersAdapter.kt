package com.example.any1.feature_group.domain.adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.any1.feature_group.domain.interfaces.OnMemberClickListener
import com.example.any1.feature_group.domain.interfaces.OnMenuClickListener
import com.example.any1.feature_group.domain.model.MemberModel
import com.bumptech.glide.Glide
import com.example.any1.R
import com.example.any1.feature_group.domain.interfaces.OnConnectClickListener
import com.google.android.material.button.MaterialButton
import com.mikhaellopez.circularimageview.CircularImageView

class MembersAdapter(val context : Context, val onMemberClickListener: OnMemberClickListener,
                     val onMenuClickListener : OnMenuClickListener,
                     val onConnectClickListener: OnConnectClickListener,
                     val isAdmin : Boolean) : RecyclerView.Adapter<MembersAdapter.MessageHolder>() {

    private val membersArrayList = ArrayList<MemberModel>()
    private val membersList = ArrayList<String>()
    private val sharedpref = context.getSharedPreferences(context.packageName+"user", MODE_PRIVATE)
    inner class MessageHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val membername : TextView = itemView.findViewById(R.id.membername)
        val memberusername : TextView = itemView.findViewById(R.id.memberusername)
        val connect : MaterialButton = itemView.findViewById(R.id.groupmemberconnect)
        val groupmenu : ImageView = itemView.findViewById(R.id.groupmenu)
        val imageView : CircularImageView = itemView.findViewById(R.id.memberpfp)

        init {
            memberusername.setOnClickListener {
                onMemberClickListener.onGroupMemberClicked(adapterPosition,membersArrayList)
            }
            membername.setOnClickListener {
                onMemberClickListener.onGroupMemberClicked(adapterPosition,membersArrayList)
            }
            imageView.setOnClickListener{
                onMemberClickListener.onGroupMemberClicked(adapterPosition,membersArrayList)
            }

            groupmenu.setOnClickListener{
                onMenuClickListener.onMenuClicked(adapterPosition,membersArrayList)
            }

            connect.setOnClickListener {
                onConnectClickListener.onConnectClicked(adapterPosition,membersArrayList)
            }

        }
    }

    fun setMembersList(arrayList: ArrayList<MemberModel>){
        membersArrayList.addAll(arrayList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.membersinglerow,parent,false)
        return MessageHolder(view)
    }

    fun clearList(){
        membersArrayList.clear()
    }

    fun notifychange(){
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        Log.d("notified","notified")
        holder.membername.text = membersArrayList[position].membername
        holder.memberusername.text = membersArrayList[position].memberusername
        if(membersArrayList[position].connected){
            holder.connect.backgroundTintList = ContextCompat.getColorStateList(context, R.color.darkgreen)
            holder.connect.text = "Connected"
        }else{
            holder.connect.backgroundTintList = ContextCompat.getColorStateList(context, R.color.fblue)
            holder.connect.text = "Connect"
        }
        if(membersArrayList[position].membername == sharedpref.getString("displayname","") && membersArrayList[position].memberusername == sharedpref.getString("username","")){
            holder.connect.visibility = View.INVISIBLE
            holder.groupmenu.visibility = View.INVISIBLE
        }else{
            holder.connect.visibility = View.VISIBLE
            holder.groupmenu.visibility = View.VISIBLE
        }
        if(membersArrayList[position].isAdmin){
            val username = membersArrayList[position].memberusername
            holder.memberusername.text = "Admin • $username"
        }else{
            holder.memberusername.text = membersArrayList[position].memberusername
        }

        if(membersArrayList[position].uri!="" && membersArrayList[position].uri!="male" && membersArrayList[position].uri!="female" ) Glide.with(context).load(membersArrayList[position].uri).circleCrop().into(holder.imageView)
        else if(membersArrayList[position].uri == "male")holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.gigachad))
        else if(membersArrayList[position].uri == "female")holder.imageView.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.doomergirl))
    }

    override fun getItemCount(): Int {
        return membersArrayList.size
    }

}