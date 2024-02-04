package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_group.domain.model.GroupInfoModel

interface GroupInfoChangeListener {
    fun onGroupInfoChanged(groupInfoModel: GroupInfoModel)
}