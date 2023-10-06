package com.example.messenger.mvvm

import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messenger.modal.Messages
import com.google.firebase.firestore.FirebaseFirestore
import com.example.messenger.Utils.Utils
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class MessageRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendid: String): LiveData<List<Messages>> {

        val messages = MutableLiveData<List<Messages>>()

        val uniqueid = listOf(Utils.getUiLoggedIn(), friendid).sorted()
        uniqueid.joinToString(separator = "")

        firestore.collection("Messages").document(uniqueid.toString()).collection("chats")
            .orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { value, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                val messageList = mutableListOf<Messages>()

                if (!value!!.isEmpty) {
                    value.documents.forEach { document ->
                        val messageModal = document.toObject(Messages::class.java)

                        if (messageModal!!.sender.equals(Utils.getUiLoggedIn()) && messageModal.receiver.equals(
                                friendid
                            ) ||
                            messageModal!!.sender.equals(friendid) && messageModal.receiver.equals(
                                Utils.getUiLoggedIn()
                            )
                        ) {

                            messageModal.let {
                                messageList.add(it!!)
                            }
                        }

                    }
                    messages.value = messageList
                }
            }
        return messages
    }


}