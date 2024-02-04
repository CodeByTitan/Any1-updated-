package com.example.any1.feature_group.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.any1.feature_group.domain.interfaces.GroupInfoChangeListener
import com.example.any1.feature_group.domain.model.GroupInfoModel
import com.example.any1.feature_group.data.GroupInfoRepository

class GroupInfoVM : ViewModel(), GroupInfoChangeListener {
    private val mutableGroupInfoModelList = MutableLiveData<GroupInfoModel>()
    val liveGroupInfoModelList : LiveData<GroupInfoModel> = mutableGroupInfoModelList
    private val groupInfoRepository = GroupInfoRepository(this)

    fun getGroupInfo(string: String){
       groupInfoRepository.getGroupInfo(string)
    }

    override fun onGroupInfoChanged(groupInfoModel: GroupInfoModel) {
        mutableGroupInfoModelList.postValue(groupInfoModel)
    }

    fun stopListening(){
        groupInfoRepository.removeListener()
    }

}