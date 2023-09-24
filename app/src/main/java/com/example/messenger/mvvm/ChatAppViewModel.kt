package com.example.messenger.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messenger.MyApplication
import com.example.messenger.SharedPrefs
import com.example.messenger.Utils.Utils
import com.example.messenger.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatAppViewModel : ViewModel() {
    val message = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()


    init {
        getCurrentUser()
    }

    fun getUsers(): LiveData<List<Users>> {
        return usersRepo.getUsers()
    }

    // lấy thông tin user hiện tại đang đăng nhập
    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        firestore.collection("Users").document(Utils.getUiLoggedIn())
            .addSnapshotListener { value, error ->
                if (value!!.exists() && value != null) {
                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users?.imageUrl!!

                    val mysharePrefs = SharedPrefs(context)
                    mysharePrefs.setValue("username", users.username!!)
                }
            }
    }

}