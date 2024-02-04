package com.example.any1.feature_login.domain.interfaces

import com.example.any1.feature_login.domain.model.SavedAccountModel


interface AccountClickListener {
    fun onAccountClick(savedAccountModel: SavedAccountModel)
}