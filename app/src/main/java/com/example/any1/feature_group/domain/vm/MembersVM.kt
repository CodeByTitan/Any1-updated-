package com.example.any1.feature_group.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.any1.feature_group.domain.interfaces.MemberListListener
import com.example.any1.feature_group.domain.interfaces.TagListListener
import com.example.any1.feature_group.domain.model.MemberModel
import com.example.any1.feature_search.domain.model.RequestModel
import com.example.any1.feature_group.data.MembersRepository
import com.example.any1.feature_group.domain.interfaces.RequestListListener

class MembersVM : ViewModel(), MemberListListener, RequestListListener, TagListListener {
    private val mutableLiveData = MutableLiveData<ArrayList<MemberModel>>()
    val liveData : LiveData<ArrayList<MemberModel>> = mutableLiveData
    private val requestList = MutableLiveData<List<RequestModel>?>()
    val liveRequestList : MutableLiveData<List<RequestModel>?> = requestList
    private val mutableTagsList = MutableLiveData<ArrayList<String>>()
    val liveTagsList : LiveData<ArrayList<String>> = mutableTagsList
    private val membersRepository = MembersRepository(this,this,this)

    fun getMembers(string: String){
        membersRepository.getMemberData(string)
    }

    fun getRequests(string: String){
        membersRepository.getRequests(string)
    }

    fun stopListeningForGroupInfo(){
        membersRepository.removeListener()
    }

    fun getTags(string: String){
        membersRepository.getGroupTags(string)
    }

    override fun showMemberList(arrayList: ArrayList<MemberModel>) {
        mutableLiveData.postValue(arrayList)
    }

    override fun showRequestList(arrayList: List<RequestModel>?) {
       requestList.postValue(arrayList)
    }

    override fun showTagList(arrayList: ArrayList<String>) {
        mutableTagsList.postValue(arrayList)
    }
}