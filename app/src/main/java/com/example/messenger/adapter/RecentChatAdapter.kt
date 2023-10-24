package com.example.messenger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.MyApplication
import com.example.messenger.R
import com.example.messenger.modal.RecentChats
import de.hdodenhof.circleimageview.CircleImageView

class RecentChatAdapter : RecyclerView.Adapter<RecentChatHolder>() {

    private var listofchats = listOf<RecentChats>()
    private var listener: onRecentChatClicked? = null
    private var recentModal = RecentChats()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist, parent, false)
        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
        return listofchats.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {

        val recentchatlist = listofchats[position]
        recentModal = recentchatlist

        holder.username.setText(recentchatlist.name)

        val themessage = recentchatlist.message!!.split(" ").take(4).joinToString(" ")
        val makelastmessage = "${recentchatlist.person}: ${themessage}"
        holder.lastMessage.setText(makelastmessage)

        Glide.with(holder.itemView.context).load(recentchatlist.friendimage).into(holder.imageView)

        holder.timeView.setText(recentchatlist.time!!.substring(0, 5))

        holder.itemView.setOnClickListener {
            listener?.getOnRecentChatClicked(position, recentchatlist)
        }

    }

    fun removeChat(position: Int) {
        val removedChat = listofchats[position]
        listofchats = listofchats.filter { it.friendid != removedChat.friendid }
        notifyItemRemoved(position)
    }


    fun getItemAtPosition(position: Int): RecentChats {
        return listofchats[position]
    }

    fun setOnRecentChatListener(listener: onRecentChatClicked) {
        this.listener = listener
    }

    fun setOnRecentList(list: List<RecentChats>) {
        this.listofchats = list
        
    }
}

class RecentChatHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
    val imageView: CircleImageView = itemview.findViewById(R.id.recentChatImageView)
    val username: TextView = itemview.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemview.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemview.findViewById(R.id.recentChatTextTime)
}

interface onRecentChatClicked {
    fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats)
}