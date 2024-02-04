package com.example.any1.feature_group.domain.interfaces

import com.example.any1.feature_group.domain.model.MemberModel

interface OnConnectClickListener {
    fun onConnectClicked(position : Int , membersArrayList: ArrayList<MemberModel>)
}