package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_search.domain.model.RequestModel

interface RequestListListener {
    fun showRequestList(arrayList: List<RequestModel>?)
}