package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_search.domain.model.RequestModel

interface OnRequestClickListener {
    fun onRequestClicked(position : Int , requestList : ArrayList<RequestModel>)
}