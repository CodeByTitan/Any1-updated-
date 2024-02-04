package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_group.domain.model.MemberModel

interface OnMenuClickListener {
    fun onMenuClicked(position : Int, memberModeList : ArrayList<MemberModel>)
}