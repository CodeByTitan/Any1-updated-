package com.example.any1.feature_group.data

import com.example.any1.feature_group.domain.interfaces.MemberListListener
import com.example.any1.feature_group.domain.interfaces.RequestListListener
import com.example.any1.feature_group.domain.interfaces.TagListListener
import com.example.any1.feature_group.domain.model.MemberModel
import com.example.any1.feature_search.domain.model.RequestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class MembersRepository(val memberListListener : MemberListListener, val requestListListener: RequestListListener, val tagListListener: TagListListener) {
    private val auth = FirebaseAuth.getInstance()
    private var membersArrayList = ArrayList<MemberModel>()
    private var membersList = ArrayList<String>()
    private var adminList = ArrayList<String>()
    private val firestore = FirebaseFirestore.getInstance()
    private var finalRequestList : List<RequestModel> ?= null
    private var isAdmin = false
    var listener: ListenerRegistration? = null
    var taglistener: ListenerRegistration? = null
    var memberlistener: ListenerRegistration? = null
    private var isConnected = false
    private lateinit var membername : String
    private lateinit var username : String
    private lateinit var uri : String
    private var tagList = ArrayList<String>()
    private lateinit var model : MemberModel
    private var requestList = ArrayList<RequestModel>()
    private var requestList2 = ArrayList<RequestModel>()
    fun getMemberData(tag : String){
        memberlistener = firestore.collection("groups").document(tag).addSnapshotListener{
            value,error->
            if (value != null) {
                membersArrayList.clear()
                adminList.clear()
                if(value.get("members")!=null && value.get("admins")!=null){
                    membersList.addAll(value.get("members") as ArrayList<String>)
                    adminList.addAll(value.get("admins") as ArrayList<String>)
                    for(i in membersList){
                        firestore.collection("users").document(i).get().addOnSuccessListener {
                            membername = it.getString("displayname").toString()
                            username = it.getString("username").toString()
                            uri = it.getString("imageurl").toString()
                            firestore.collection("users").document(auth.currentUser!!.uid).collection("connections").document(i).get().addOnSuccessListener {
                                userdoc ->
                                isConnected = userdoc.exists()
                            }.addOnFailureListener{isConnected = false}
                            isAdmin = adminList.contains(i)
                            model = MemberModel(membername,username,uri,isConnected,isAdmin,i)
                            membersArrayList.add(model)
                            val filteredList = membersArrayList.distinct()
                            val filteredArrayList = ArrayList(filteredList)
                            memberListListener.showMemberList(filteredArrayList)
                        }
                    }
                }
            }
        }
    }

    fun getGroupTags(tag: String){
        taglistener = firestore.collection("groups").document(tag).addSnapshotListener{
            value, error ->
            if(value!=null){
                tagList.clear()
                if(value.get("searchtags")!= null){
                    tagList = value.get("searchtags") as ArrayList<String>
                    tagListListener.showTagList(tagList)
                }
            }
        }
    }

    fun removeListener(){
        listener?.remove()
        taglistener?.remove()
        memberlistener?.remove()
    }
    fun getRequests(tag : String){
        listener = firestore.collection("groups").document(tag).addSnapshotListener {
            document, error ->
            requestList.clear()
            if(document!=null) {
                requestList.clear()
                requestList2.clear()
                if(document.get("requests")!=null) {
                    requestList.clear()
                    val tempReqList = document.get("requests") as ArrayList<String>
                    if(tempReqList.size!=0){
                        CoroutineScope(Main).launch{
                            for (id in tempReqList) {
                                firestore.collection("users").document(id).get().addOnSuccessListener { doc ->
                                        val name = doc.getString("displayname").toString()
                                        val username = doc.getString("username").toString()
                                        val uri = doc.getString("imageurl").toString()
                                        val model = RequestModel(name, username, uri, false, id)
                                        requestList.add(model)
                                        finalRequestList = requestList.distinct()
                                    if(finalRequestList!=null) requestListListener.showRequestList(finalRequestList)
                                }
                            }
//                            job1.join()
                        }

                        if(finalRequestList!=null) {
                            requestListListener.showRequestList(
                            finalRequestList!!
                        )}
                    }else{
                        requestList.clear()
                        requestListListener.showRequestList(requestList)
                    }
                }else{
                    requestList.clear()
                    requestListListener.showRequestList(requestList)
                }
            }
        }
    }

//    private suspend fun getRequestInformation(tempReqList : ArrayList<String>) : List<RequestModel>?{
//        coroutineScope {
//            for (id in tempReqList) {
//                firestore.collection("users").document(id).get()
//                    .addOnSuccessListener { doc ->
//                        val name = doc.getString("displayname").toString()
//                        val username = doc.getString("username").toString()
//                        val uri = doc.getString("imageurl").toString()
//                        val model = RequestModel(name, username, uri, false, id)
//                        requestList.add(model)
//                        finalRequestList = requestList.distinct()
////                                    requestListListener.showRequestList(finalRequestList)
//                    }
//            }
//        }
//    }
}


//                            val list = async { getRequestInformation(tempReqList) }
//                            requestListListener.showRequestList(requestList2)
//                            for (id in tempReqList) {
//                                firestore.collection("users").document(id).get()
//                                    .addOnSuccessListener { doc ->
//                                        val name = doc.getString("displayname").toString()
//                                        val username = doc.getString("username").toString()
//                                        val uri = doc.getString("imageurl").toString()
//                                        val model = RequestModel(name, username, uri, false, id)
//                                        requestList.add(model)
//                                        finalRequestList = requestList.distinct()
////                                    requestListListener.showRequestList(finalRequestList)
//                                    }
//                            }