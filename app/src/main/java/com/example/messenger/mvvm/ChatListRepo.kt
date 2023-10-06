package com.example.messenger.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messenger.modal.RecentChats
import com.google.firebase.firestore.FirebaseFirestore
import com.example.messenger.Utils.Utils
import com.google.firebase.firestore.Query


class ChatListRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList(): LiveData<List<RecentChats>> {

        val mainChatList = MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation${Utils.getUiLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                val chatlist = mutableListOf<RecentChats>()
                value?.forEach { document ->
                    val recentmodal = document.toObject(RecentChats::class.java)

                    if (recentmodal.sender.equals(Utils.getUiLoggedIn())) {
                        recentmodal.let {
                            chatlist.add(it)
                        }
                    }
                }
                mainChatList.value = chatlist
            }
        return mainChatList
    }
}