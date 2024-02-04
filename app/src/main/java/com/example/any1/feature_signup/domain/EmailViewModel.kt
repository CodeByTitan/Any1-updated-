package com.example.any1.feature_signup.domain

import androidx.lifecycle.ViewModel

class EmailViewModel:ViewModel() {
    var email = ""
    @JvmName("setEmail1")
    fun setEmail(input:String){
        email = input
    }
    @JvmName("getEmail1")
    fun getEmail(input:String){
        email = input
    }
}