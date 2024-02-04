package com.example.any1.feature_profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.any1.feature_group.presentation.GroupInfo
import com.example.any1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewProfile : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var name : String
    private lateinit var imageuri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewprofile)
        val username = intent.getStringExtra("memberusername")
        firestore.collection("users").whereEqualTo("username",username).addSnapshotListener{
                it, _ ->
            if(it!=null){
                for(document in it.documents){
                    imageuri = document.getString("imageurl")!!.toUri()
                    name = document.getString("displayname").toString()
                }
            }
        }

    }

    private fun slideleft() {
        if (intent.getStringExtra("groupinfo") == "groupinfo") {
            val intent = Intent(this, GroupInfo::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slideleft2slow,
                R.anim.slideleftslow
            )
        }
    }
}