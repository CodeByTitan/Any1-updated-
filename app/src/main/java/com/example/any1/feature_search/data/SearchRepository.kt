package com.example.any1.feature_search.data

import com.example.any1.feature_search.domain.model.SearchModel
import com.example.any1.feature_search.domain.model.SearchUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class SearchRepository {
    val firestore = FirebaseFirestore.getInstance()
    private var temporaryUserList = ArrayList<SearchUserModel>()
    private var temporaryList = ArrayList<SearchModel>()
    private lateinit var groupname : String
    private lateinit var groupimageurl : String
    private var connectionscount = 0
    private var isApprovalRequired : Boolean = false
    private lateinit var userdisplayname : String
    private lateinit var username : String
    private lateinit var userimageurl : String
    private var isConnected = false
    private lateinit var grouptag : String
    private var isRequested = false
    private lateinit var memberList : ArrayList<String>
    private var requestList = ArrayList<String>()
    private var membercount : Long = 0
    val auth = FirebaseAuth.getInstance()

    fun getUserNameFromFirestore(string: String): ArrayList<SearchUserModel>{
        connectionscount = 0
        temporaryUserList.clear()
        firestore.collection("users").orderBy("username").startAt(string.trim()).endAt((string.trim() + "\uf8ff")).get().addOnSuccessListener {
           addUserData(it)
        }
        firestore.collection("users").orderBy("displayname").startAt(string.trim()).endAt((string.trim() + "\uf8ff")).get().addOnSuccessListener {
            addUserData(it)
        }
        return temporaryUserList
    }

    private fun addUserData(it : QuerySnapshot){
        for (i in it.documents) {
            username = i.getString("username").toString()
            userdisplayname = i.getString("displayname").toString()
            userimageurl = i.getString("imageurl").toString()
            firestore.collection("users").document(i.id).collection("connections")
                .document(auth.currentUser!!.uid).get().addOnSuccessListener {
                    isConnected = true
                    isRequested = false
                }.addOnFailureListener {
                    firestore.collection("users").document(i.id).collection("requests")
                        .document(auth.currentUser!!.uid).get().addOnSuccessListener {
                            isConnected = false
                            isRequested = true
                        }.addOnFailureListener {
                            isConnected = false
                            isRequested = false
                        }
                }
            val model = SearchUserModel(
                userdisplayname,
                username,
                userimageurl,
                isConnected,
                isRequested
            )
            temporaryUserList.add(model)
        }
    }
    fun getTagsFromFirestore(string: String): ArrayList<SearchModel>{
        firestore.collection("groups").orderBy("searchtag").startAt(string).get().addOnSuccessListener {
            if(!it.isEmpty){
                connectionscount = 0
                temporaryList.clear()
                for (document in it.documents){
                    groupname = document.getString("name").toString()
                    groupimageurl = document.getString("imageurl").toString()
                    isApprovalRequired = document.getBoolean("approval") == true
                    membercount = document.get("membercount") as Long
                    grouptag = document.id
                    memberList = document.get("members") as ArrayList<String>
                    requestList = document.get("requests") as ArrayList<String>
                    for(i in memberList) {
                        firestore.collection("users").document(auth.currentUser!!.uid)
                            .collection("connections").document(i).get().addOnSuccessListener {
                                connectionscount++
                        }
                    }
                    val model = SearchModel(groupname,groupimageurl,isApprovalRequired,membercount,grouptag,memberList,requestList,connectionscount)
                    temporaryList.add(model)
                }
            }
        }
        return temporaryList
    }

    fun getNamesFromFirestore(string: String): ArrayList<SearchModel>{
        firestore.collection("groups").orderBy("name").startAt(string).get().addOnSuccessListener {
            if(!it.isEmpty){
                connectionscount =0
                temporaryList.clear()
                for (document in it.documents){
                    groupname = document.getString("name").toString()
                    groupimageurl = document.getString("imageurl").toString()
                    isApprovalRequired = document.getBoolean("approval") == true
                    membercount = document.get("membercount") as Long
                    grouptag = document.id
                    memberList = document.get("members") as ArrayList<String>
                    if(isApprovalRequired){
                        if(document.get("requests")!=null){
                            requestList = document.get("requests") as ArrayList<String>
                        }
                    }
                    for(i in memberList) {
                        firestore.collection("users").document(auth.currentUser!!.uid)
                            .collection("connections").document(i).get().addOnSuccessListener {
                                connectionscount++
                            }
                    }
                    val model = SearchModel(groupname,groupimageurl,isApprovalRequired,membercount,grouptag,memberList,requestList,connectionscount)
                    temporaryList.add(model)
                }
            }
        }
        return temporaryList
    }

    fun getGroupTagFromFirestore(string: String) : ArrayList<SearchModel>{
        temporaryList.clear()
        connectionscount = 0
        firestore.collection("groups").document(string).get().addOnSuccessListener {
            document->
            if(document.exists()) {
                groupname = document.getString("name").toString()
                groupimageurl = document.getString("imageurl").toString()
                isApprovalRequired = document.getBoolean("approval") == true
                membercount = document.get("membercount") as Long
                grouptag = document.id
                memberList = document.get("members") as ArrayList<String>
                if(isApprovalRequired){
                    if(document.get("requests")!=null){
                        requestList = document.get("requests") as ArrayList<String>
                    }
                }
                for (i in memberList) {
                    firestore.collection("users").document(auth.currentUser!!.uid)
                        .collection("connections").document(i).get().addOnSuccessListener {
                            connectionscount++
                        }
                }
                val model = SearchModel(
                    groupname,
                    groupimageurl,
                    isApprovalRequired,
                    membercount,
                    grouptag,
                    memberList,
                    requestList,
                    connectionscount
                )
                temporaryList.add(model)
            }
        }
        return temporaryList
    }

}