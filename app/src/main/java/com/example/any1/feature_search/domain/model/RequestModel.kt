package com.example.any1.feature_search.domain.model

import android.net.Uri

data class RequestModel(val name : String, val username : String, val uri: String, var isChecked : Boolean, val id : String)