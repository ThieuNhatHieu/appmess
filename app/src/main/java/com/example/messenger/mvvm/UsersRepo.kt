package com.example.messenger.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messenger.Utils.Utils
import com.example.messenger.modal.Users
import com.google.firebase.firestore.FirebaseFirestore

class UsersRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getUsers(): LiveData<List<Users>>{
        val users = MutableLiveData<List<Users>>()

        firestore.collection("Users").addSnapshotListener{snapshot, exception ->

            if (exception != null){
                return@addSnapshotListener
            }

            val userlist = mutableListOf<Users>()
            snapshot?.documents?.forEach{document->
                val user = document.toObject(Users::class.java)
                if (user!!.userid != Utils.getUiLoggedIn()){
                    user.let {
                        userlist.add(it)
                    }
                }
                users.value = userlist
            }
        }
            return users
    }
}