package com.example.any1.feature_group.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.any1.feature_group.domain.interfaces.GroupListListener
import com.example.any1.feature_group.domain.model.GroupModel
import com.example.any1.feature_group.data.GroupRepository


class GroupVM():ViewModel(), GroupListListener {
    private val mutableLiveData: MutableLiveData<ArrayList<GroupModel>> = MutableLiveData<ArrayList<GroupModel>>()
    val getGroups: LiveData<ArrayList<GroupModel>> = mutableLiveData
    var groupRepository = GroupRepository(this)
    private val userid = MutableLiveData<String>()
    val liveuserid : LiveData<String> = userid
    init {
        groupRepository.getGroups()
    }
    fun getGroups(){
        groupRepository.getGroups()
    }
    fun updateAuthId(string: String){
        userid.postValue(string)
    }
    override fun showListOfUser(groupModelList: ArrayList<GroupModel>) {
        mutableLiveData.value = groupModelList
    }
}