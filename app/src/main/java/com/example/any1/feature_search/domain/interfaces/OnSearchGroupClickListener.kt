package com.example.any1.feature_search.domain.interfaces

import com.example.any1.feature_search.domain.model.SearchModel

interface OnSearchGroupClickListener {
    fun OnGroupClicked(position: Int, groupModelList: ArrayList<SearchModel>)
}