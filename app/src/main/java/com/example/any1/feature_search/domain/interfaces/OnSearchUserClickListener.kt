package com.example.any1.feature_search.domain.interfaces

import com.example.any1.feature_search.domain.model.UserModel


interface OnSearchUserClickListener {
    fun onUserClicked(position : Int, userList: ArrayList<UserModel>)
}