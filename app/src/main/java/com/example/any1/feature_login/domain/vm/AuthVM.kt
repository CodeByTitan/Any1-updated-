package com.example.any1.feature_login.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.any1.feature_group.domain.interfaces.GroupListListener
import com.example.any1.feature_group.domain.model.GroupModel
import com.example.any1.feature_group.data.GroupRepository


class AuthVM():ViewModel(){
    private val userid = MutableLiveData<String>()
    val liveuserid : LiveData<String> = userid

    fun updateAuthId(string: String){
        userid.postValue(string)
    }
}