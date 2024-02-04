package com.example.any1.feature_signup.domain

import androidx.lifecycle.ViewModel

class UserPassVM : ViewModel() {
    var username = ""
    var password = ""

    @JvmName("setUsername1")
    fun setUsername(input: String) {
        username = input
    }
    @JvmName("setPassword1")
    fun setPassword(input: String) {
        username = input
    }

    @JvmName("getUsername1")
    fun getUsername(): String? {
        return username.toString()
    }
    @JvmName("getPassword1")
    fun getPassword(): String? {
        return username.toString()
    }
}