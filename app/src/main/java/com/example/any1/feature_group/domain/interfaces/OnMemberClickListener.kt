package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_group.domain.model.MemberModel

interface OnMemberClickListener {
    fun onGroupMemberClicked(position : Int, memberArrayList : ArrayList<MemberModel>)
}