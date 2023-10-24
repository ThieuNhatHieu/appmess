package com.example.messenger.modal

data class Messages (
    val sender : String? = "",
    val receiver : String? = "",
    val message : String? = "",
    val time : String? = "",
    val messageType: String? = "", // Loại tin nhắn: "text" hoặc "sticker"

){
    val id: String get() = "$sender-$receiver-$message-$time"
}

