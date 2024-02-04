package com.example.any1.feature_search.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.any1.feature_search.data.SearchRepository
import com.example.any1.feature_search.domain.model.SearchModel
import com.example.any1.feature_search.domain.model.SearchUserModel

class SearchViewModel: ViewModel() {
    private val mutableLiveData : MutableLiveData<ArrayList<SearchModel>> = MutableLiveData()
    private val mutableUserList : MutableLiveData<ArrayList<SearchUserModel>> = MutableLiveData()
    var liveData : LiveData<ArrayList<SearchModel>> = mutableLiveData
    var liveUserList : LiveData<ArrayList<SearchUserModel>> = mutableUserList
    val searchstring : MutableLiveData<String> = MutableLiveData()
    var livestring : LiveData<String> = searchstring
    private val searchRepository = SearchRepository()


    fun setString(string: String){
        searchstring.value = string
    }

    fun getUsersByUsername(){
        mutableUserList.postValue(searchRepository.getUserNameFromFirestore(searchstring.value.toString()))
    }

    fun getGroupsByTags(){
        val arrayList = searchRepository.getTagsFromFirestore(searchstring.value.toString())
        mutableLiveData.postValue(arrayList)
    }

    fun getGroupsByNames(){
        mutableLiveData.postValue(searchRepository.getNamesFromFirestore(searchstring.value.toString()))
    }

    fun getGroupsByGroupTag (){
        mutableLiveData.postValue(searchRepository.getGroupTagFromFirestore(searchstring.value.toString()))
    }
}