package com.example.messenger.fragments

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.MyEditText
import com.example.messenger.R
import com.example.messenger.Utils.Utils
import com.example.messenger.adapter.MessageAdapter
import com.example.messenger.databinding.FragmentChatBinding
import com.example.messenger.modal.Messages
import com.example.messenger.mvvm.ChatAppViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class ChatFragment : Fragment() {

    private lateinit var args: ChatFragmentArgs
    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var chatAppViewModel: ChatAppViewModel
    private lateinit var chatToolbar: Toolbar
    private lateinit var myEditText: MyEditText;
    private lateinit var circleImageView: CircleImageView
    private lateinit var tvUserName : TextView
    private lateinit var tvStatus : TextView
    private lateinit var backbtn : ImageView
    private lateinit var messageAdapter: MessageAdapter
    private var stickerUrl: String? = null
    private lateinit var storageRef: StorageReference
    lateinit var storage: FirebaseStorage
    var uri: Uri? = null
    lateinit var bitmap: Bitmap



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        chatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)
        return chatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // lưu trữ thông tin users lấy thông tin users từ HomeFragment sang ChatFragment
        args = ChatFragmentArgs.fromBundle(requireArguments())
        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)
        chatToolbar = view.findViewById(R.id.toolBarChat)
        myEditText = view.findViewById(R.id.editTextMessage);
        circleImageView = chatToolbar.findViewById(R.id.chatImageViewUser)
        tvStatus = view.findViewById(R.id.chatUserStatus)
        tvUserName = chatToolbar.findViewById(R.id.chatUserName)
        backbtn = chatToolbar.findViewById(R.id.chatBackBtn)


        backbtn.setOnClickListener {
            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)
        }

        Glide.with(requireContext()).load(args.users.imageUrl).into(circleImageView)
        tvStatus.setText(args.users.status)
        tvUserName.setText(args.users.username)
        myEditText.setChatAppViewModel(chatAppViewModel)



        chatBinding.viewModel = chatAppViewModel
        chatBinding.lifecycleOwner = viewLifecycleOwner
        chatAppViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        // Quan sát LiveData để theo dõi sự thay đổi của đường dẫn sticker
        chatAppViewModel.stickerUrl.observe(viewLifecycleOwner, { url ->
            if (url != null) {
                stickerUrl = url
                Log.d("ChatFragment", "Received sticker: $stickerUrl")
                // Xử lý đường dẫn sticker ở đây, ví dụ: hiển thị nó trên giao diện người dùng
            }
        })


        chatBinding.sendBtn.setOnClickListener {
            val messageContent = chatBinding.editTextMessage.text.toString()

            if (stickerUrl != null) {
                Log.d("ChatFragment", "Sending sticker: $stickerUrl")
                chatAppViewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    args.users.userid!!,
                    args.users.username!!,
                    args.users.imageUrl!!,
                    stickerUrl!!,  // Sử dụng đường dẫn sticker từ LiveData
                    "sticker"
                )
                stickerUrl = null

            } else if (messageContent.isNotEmpty()) {
                Log.d("ChatFragment", "Sending text message: $messageContent")
                chatAppViewModel.sendMessage(
                    Utils.getUiLoggedIn(),
                    args.users.userid!!,
                    args.users.username!!,
                    args.users.imageUrl!!,
                    messageContent,
                    "text"
                )
            }
        }
        fun setStickerUrl(url: String) {
            stickerUrl = url
        }

//        chatBinding.sendBtn.setOnClickListener{
//            chatAppViewModel.sendMessage(Utils.getUiLoggedIn(), args.users.userid!!, args.users.username!!, args.users.imageUrl!!, )
//
//
//        }


        chatAppViewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {


            initRecycleView(it)
        })
    }

    private fun initRecycleView(it: List<Messages>) {
        messageAdapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(context)
        chatBinding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true
        messageAdapter.setMessageList(it)
        messageAdapter.notifyDataSetChanged()
        chatBinding.messagesRecyclerView.adapter = messageAdapter
    }

}