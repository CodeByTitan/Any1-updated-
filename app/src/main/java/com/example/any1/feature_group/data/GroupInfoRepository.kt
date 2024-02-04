package com.example.any1.feature_group.data

import com.example.any1.feature_group.domain.interfaces.GroupInfoChangeListener
import com.example.any1.feature_group.domain.model.GroupInfoModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class GroupInfoRepository (private val groupInfoChangeListener: GroupInfoChangeListener){
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var groupInfoModelList = ArrayList<GroupInfoModel>()
    private var requests = ArrayList<String>()
    private var adminList = ArrayList<String>()
    private lateinit var model : GroupInfoModel
    var listener: ListenerRegistration? = null

    fun getGroupInfo(string: String) : GroupInfoModel {
        listener = firestore.collection("groups").document(string).addSnapshotListener() { value, _ ->
            if(value!=null){
                val name = value.getString("name").toString()
                adminList = value.get("admins") as ArrayList<String>
//                val stringAdminList = value.get("admins")as ArrayList<*>
//                adminList = stringAdminList as ArrayList<String>
//                Do what you need to do with your ArrayList
                val hidetags = value.getBoolean("hidetags")!!
                val imageurl = value.getString("imageurl").toString()
                val membercount = value.get("membercount") as Long
                val members = value.get("members") as ArrayList<String>
                val namelock = value.getBoolean("namelock")!!
                val nosimping = value.getBoolean("nosimping")!!
                val approval = value.getBoolean("approval")!!
                val rankupdates = value.getBoolean("rankupdates")!!
                val accessiblity = value.getString("accessibility").toString()
                if (value.get("requests") != null) {
                    requests = value.get("requests") as ArrayList<String>
                }
                val searchTagsList = value.get("searchtag") as ArrayList<String>
                val videocall = value.getBoolean("videocall")!!
                model = GroupInfoModel(
                    name,
                    adminList = adminList,
                    hidetags = hidetags,
                    imageurl = imageurl,
                    membercount = membercount,
                    membersList = members,
                    namelock = namelock,
                    nosimping = nosimping,
                    rankupdates = rankupdates,
                    searchTags = searchTagsList,
                    videocall = videocall,
                    approval = approval,
                    tag = string,
                    requestList = requests,
                    accessibility = accessiblity
                )
                groupInfoChangeListener.onGroupInfoChanged(model)
            }
        }
        return if(::model.isInitialized){
            model
//            groupInfoChangeListener.onGroupInfoChanged(model)
        }else{
            val temporaryModel = GroupInfoModel("", arrayListOf(),false,"",
                false,"",true,false,false,
                arrayListOf(),false, arrayListOf(), arrayListOf(),0,"public")
            temporaryModel
        }
    }
    fun removeListener(){
        listener?.remove()
    }
}