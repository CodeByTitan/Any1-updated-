package com.example.any1.feature_chat.domain.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.any1.feature_chat.data.ChatRepository
import com.example.any1.feature_chat.domain.interfaces.MessageReceiveListener
import com.example.any1.feature_chat.domain.model.ChatModel
import kotlinx.coroutines.launch


class ChatViewModel: ViewModel(), MessageReceiveListener {
    private val mutableLiveData: MutableLiveData<ArrayList<ChatModel>?> = MutableLiveData<ArrayList<ChatModel>?>()
    var getGroupMessages : MutableLiveData<ArrayList<ChatModel>?> = mutableLiveData
    private val chatRepository = ChatRepository(this)

    fun getMessages(grouptag : String){
        viewModelScope.launch {
            chatRepository.getMessages(grouptag)
        }
    }

    override fun OnMessageReceived(messageModels: ArrayList<ChatModel>) {
        mutableLiveData.postValue(messageModels)
    }

    fun stopReceiveingMessages() {
        mutableLiveData.postValue(null)
    }
}