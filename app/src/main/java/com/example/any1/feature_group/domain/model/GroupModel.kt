package com.example.any1.feature_group.domain.model

import android.net.Uri

data class GroupModel(val item: String, var uri: String, val tag : String, var isMuted: Boolean ) {
    init {
        isMuted = false
    }
}