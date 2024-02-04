package com.example.any1.feature_chat.data

import com.example.any1.feature_chat.domain.interfaces.MessageReceiveListener
import com.example.any1.feature_chat.domain.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChatRepository ( val messageReceiveListener : MessageReceiveListener) {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val messageList: ArrayList<ChatModel> = ArrayList()
    private var listWithoutDotsStr = ArrayList<String>()
    private val temporaryList = ArrayList<ChatModel>()
    private lateinit var finalList : List<String>
    private val messageListForADay = ArrayList<ChatModel>()

    fun getMessages(grouptag: String) {
        firestore.collection("groups").document(grouptag).collection("messages").orderBy("timestamp",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    messageList.clear()
                    for (i in value.documents) {
                        val doc = i.id
                        firestore.collection("groups").document(grouptag).collection("messages").document(doc).collection("messages").orderBy("timestamp", Query.Direction.DESCENDING).addSnapshotListener{
                            value,error ->
                            if(value!=null) {
                                val date = Date(System.currentTimeMillis())
                                val formatter1= SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy")
                                val formatter2 = SimpleDateFormat("dd.MM.yyyy")
                                val date1 = formatter1.parse(date.toString())?.let { formatter2.format(it) }
                                if(date1 == doc){
                                    if(messageList.size!=0){
                                        for (document in value.documents) {
                                            val senderid = document.getString("sender").toString()
                                            val message = document.getString("message").toString()
                                            val senderpfpurl = document.getString("senderpfpuri").toString()
                                            val model = ChatModel(message, document.id, senderid, senderpfpurl)
                                            temporaryList.add(model)
                                        }
                                        temporaryList.removeAll(messageList)
                                        messageList.addAll(0,temporaryList)
                                        messageReceiveListener.OnMessageReceived(messageList)
                                    }else{
                                        for (document in value.documents) {
                                            val senderid = document.getString("sender").toString()
                                            val message = document.getString("message").toString()
                                            val senderpfpurl = document.getString("senderpfpuri").toString()
                                            val model = ChatModel(message, document.id, senderid, senderpfpurl)
                                            messageList.add(model)
                                            messageReceiveListener.OnMessageReceived(messageList)
                                        }
                                    }
                                }else{
                                    for (document in value.documents) {
                                        val senderid = document.getString("sender").toString()
                                        val message = document.getString("message").toString()
                                        val senderpfpurl = document.getString("senderpfpuri").toString()
                                        val model = ChatModel(message, document.id, senderid, senderpfpurl)
                                        messageList.add(model)
                                        messageReceiveListener.OnMessageReceived(messageList)
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }
    private fun getDocumentsSortedByDate(dates : ArrayList<String>): List<String>{
        for (it in dates) {
            val dotRemovedString = it.replace(".","")
            listWithoutDotsStr.add(dotRemovedString)
        }
        listWithoutDotsStr.sortBy { it.substring(0,2) }
        listWithoutDotsStr.sortBy { it.substring(2,4) }
        listWithoutDotsStr.sortBy {it.substring(it.length-4,it.length)}
//        Log.d("list",listWithoutDotsStr.toString())
        val listwithdots = java.util.ArrayList<String>()
        for (i in listWithoutDotsStr){
            val stringBuilder = StringBuilder(i)
            stringBuilder.insert(2,".")
            stringBuilder.insert(i.length-3,".")
            listwithdots.add(stringBuilder.toString())
        }
        finalList = listwithdots.reversed()
//        Log.d("list",listwithdots.toString())
//        Log.d("list",finalList.toString())
        return finalList
    }
}
