package com.example.any1.feature_chat.domain.interfaces

import com.example.any1.feature_chat.domain.model.ChatModel

interface MessageReceiveListener {
    fun OnMessageReceived(messageModels: ArrayList<ChatModel>)
}