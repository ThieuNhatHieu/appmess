package com.example.messenger.mvvm

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class ChatAppViewModel {
    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
}