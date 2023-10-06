package com.example.messenger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textclassifier.ConversationActions.Message
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.R
import com.example.messenger.Utils.Utils
import com.example.messenger.modal.Messages

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessage = listOf<Messages>()
    private val LEFT = 0
    private val RIGHT = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == RIGHT){
            val view = inflater.inflate(R.layout.chatitemright, parent, false)
            MessageHolder(view)
        }else{
            val view = inflater.inflate(R.layout.chatitemleft, parent, false)
            MessageHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return listOfMessage.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = listOfMessage[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.messageText.setText(message.message)
        holder.timeOfSent.text = message.time?.substring(0,5)?:""

    }

    override fun getItemViewType(position: Int): Int =
        if (listOfMessage[position].sender == Utils.getUiLoggedIn()) RIGHT else LEFT

    fun setMessageList(newlist: List<Messages>){
        this.listOfMessage = newlist
    }
}

class MessageHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
    val messageText : TextView = itemview.findViewById(R.id.show_message)
    val timeOfSent : TextView = itemview.findViewById(R.id.timeView)
}