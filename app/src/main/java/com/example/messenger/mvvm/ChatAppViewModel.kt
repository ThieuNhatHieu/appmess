package com.example.messenger.mvvm

import android.view.textclassifier.ConversationActions.Message
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.util.Util
import com.example.messenger.MyApplication
import com.example.messenger.SharedPrefs
import com.example.messenger.Utils.Utils
import com.example.messenger.modal.Messages
import com.example.messenger.modal.RecentChats
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
    val messageRepo = MessageRepo()
    val recentChatRepo = ChatListRepo()

    init {
        getCurrentUser()
        getRecentChats()
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

    // gửi tin nhắn
    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {
            val context = MyApplication.instance.applicationContext

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")


            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)

            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { task ->

                    val hashMapForRecent = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUiLoggedIn(),
                        "message" to message.value!!,
                        "friendimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )

                    firestore.collection("Conversation${Utils.getUiLoggedIn()}").document(receiver)
                        .set(hashMapForRecent)

                    firestore.collection("Conversation${receiver}").document(Utils.getUiLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )
                    if (task.isSuccessful){
                        message.value = ""
                    }
                }
        }

    fun getMessages(friendid: String): LiveData<List<Messages>>{

            return messageRepo.getMessages(friendid)
    }

    fun getRecentChats(): LiveData<List<RecentChats>>{
        return recentChatRepo.getAllChatList()
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO){

        val context = MyApplication.instance.applicationContext

        val hashMapUser = hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUiLoggedIn()).update(hashMapUser).addOnCompleteListener { task->


            if (task.isSuccessful){

                Toast.makeText(context, "UPDATED", Toast.LENGTH_SHORT).show()
            }
        }

        val mySharedPrefs = SharedPrefs(context)
        val friendid = mySharedPrefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String, Any>("friendimage" to imageUrl.value!!, "name" to name.value!!, "person" to name.value!!)

        if (friendid != null){
            firestore.collection("Conversation${friendid}").document(Utils.getUiLoggedIn()).update(hashMapUpdate)

            firestore.collection("Conversation${Utils.getUiLoggedIn()}").document(friendid!!).update("person", "you")
        }

    }

}