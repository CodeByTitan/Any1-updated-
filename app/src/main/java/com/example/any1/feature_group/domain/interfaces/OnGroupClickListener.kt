package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_group.domain.model.GroupModel

interface OnGroupClickListener{
    fun OnGroupClicked(position: Int, groupModelList: ArrayList<GroupModel>)
}