package com.example.any1.feature_group.data

import com.example.any1.feature_group.domain.interfaces.GroupListListener
import com.example.any1.feature_group.domain.model.GroupModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*


class GroupRepository(grouplistener : GroupListListener) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private var groupListListener: GroupListListener = grouplistener
    val groupModelList: ArrayList<GroupModel> = ArrayList()
    private var callmuted : Boolean = false
    private var messagemuted : Boolean = false
    fun getGroups() {
        if(auth.currentUser!=null) {
            firestore.collection("groups").whereArrayContains("members", auth.currentUser!!.uid)
                .addSnapshotListener { value, error ->
                    if (value != null) {
                        groupModelList.clear()
                        for (document in value.documents) {
                            val groupname = document.getString("name")
                            val imageurl = document.getString("imageurl")
                            val gctag = document.id
                            firestore.collection("users").document(auth.currentUser!!.uid)
                                .collection("groups").addSnapshotListener { value, error ->
                                    if (value != null) {
                                        for (doc in value.documents) {
                                            if (gctag == doc.id) {
                                                messagemuted = doc.getBoolean("messagemuted")!!
                                            }
                                        }
                                    }
                                }
                            val groupModel =
                                GroupModel(groupname.toString(), imageurl!!, gctag, messagemuted)
                            groupModelList.add(0, groupModel)
                            groupListListener.showListOfUser(groupModelList)
                        }
                    }
                }
        }
    }

}