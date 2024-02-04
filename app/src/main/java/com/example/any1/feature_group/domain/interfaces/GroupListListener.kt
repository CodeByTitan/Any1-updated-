package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_group.domain.model.GroupModel

interface GroupListListener {
    fun showListOfUser(groupModelList: ArrayList<GroupModel>)
}