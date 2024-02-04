package com.example.any1.feature_signup.domain

import androidx.lifecycle.ViewModel

class AddAccountViewModel : ViewModel() {
    var add = false
    fun add(){
        add = true
    }
    fun dontadd(){
        add = false
    }
}