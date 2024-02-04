package com.example.any1.feature_search.domain.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.any1.feature_search.data.SearchRepository
import com.example.any1.feature_search.domain.model.SearchUserModel

class SearchUserViewModel : ViewModel() {
    private val mutableLiveData = MutableLiveData<ArrayList<SearchUserModel>>()
    val liveData : LiveData<ArrayList<SearchUserModel>> = mutableLiveData
    val searchUserRepository = SearchRepository()
}