package com.example.messenger.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.Utils.Utils
import com.example.messenger.adapter.MessageAdapter
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.databinding.FragmentChatfromHomeBinding
import com.example.messenger.modal.Messages
import com.example.messenger.mvvm.ChatAppViewModel
import de.hdodenhof.circleimageview.CircleImageView


class ChatFromHomeFragment : Fragment() {

    private lateinit var args: ChatFromHomeFragmentArgs
    private lateinit var chatfromHomeBinding: FragmentChatfromHomeBinding
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chatToolbar: Toolbar
    private lateinit var circleImageView: CircleImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvStatus: TextView
    private lateinit var backbtn: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private var stickerUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        chatfromHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)
        return chatfromHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())
        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        chatToolbar = view.findViewById(R.id.toolBarChat)
        circleImageView = chatToolbar.findViewById(R.id.chatImageViewUser)
        tvStatus = view.findViewById(R.id.chatUserStatus)
        tvUserName = chatToolbar.findViewById(R.id.chatUserName)
        backbtn = chatToolbar.findViewById(R.id.chatBackBtn)

        backbtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)
        }

        Glide.with(requireContext()).load(args.recentchats.friendimage).into(circleImageView)
        tvStatus.setText(args.recentchats.status)
        tvUserName.setText(args.recentchats.name)


        chatfromHomeBinding.viewModel = chatAppViewModel
        chatfromHomeBinding.lifecycleOwner = viewLifecycleOwner

//        chatfromHomeBinding.sendBtn.setOnClickListener {
//            chatAppViewModel.sendMessage(
//                Utils.getUiLoggedIn(),
//                args.recentchats.friendid!!,
//                args.recentchats.name!!,
//                args.recentchats.friendimage!!
//            )
//
//
//        }

        chatfromHomeBinding.sendBtn.setOnClickListener {
            val messageContent = chatfromHomeBinding.editTextMessage.text.toString()
            if (stickerUrl != null && stickerUrl!!.isNotEmpty()) {
                // Nếu stickerUrl không rỗng, người dùng đã chọn gửi sticker
                chatAppViewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    args.recentchats.friendid!!,
                    args.recentchats.name!!,
                    args.recentchats.friendimage!!,
                    stickerUrl!!,
                    "sticker" // Đặt loại tin nhắn là "sticker"
                )
            } else if (messageContent.isNotEmpty()) {
                // Nếu messageContent không rỗng, người dùng đã chọn gửi tin nhắn văn bản

                chatAppViewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    args.recentchats.friendid!!,
                    args.recentchats.name!!,
                    args.recentchats.friendimage!!,
                    messageContent,
                    "text" // Đặt loại tin nhắn là "text"
                )
            }
        }
        fun setStickerUrl(url: String) {
            stickerUrl = url
        }


        chatAppViewModel.getMessages(args.recentchats.friendid!!)
            .observe(viewLifecycleOwner, Observer {


                initRecycleView(it)
            })
    }

    private fun initRecycleView(it: List<Messages>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatfromHomeBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatfromHomeBinding.messagesRecyclerView.adapter = messageAdapter
    }

}


