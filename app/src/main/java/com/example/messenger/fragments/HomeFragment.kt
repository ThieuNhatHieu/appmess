package com.example.messenger.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.adapter.OnUserClickListener
import com.example.messenger.adapter.UserAdapter
import com.example.messenger.databinding.FragmentHomeBinding
import com.example.messenger.modal.Users
import com.example.messenger.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.example.messenger.SignInActivity
import com.example.messenger.adapter.RecentChatAdapter
import com.example.messenger.adapter.onRecentChatClicked
import com.example.messenger.modal.RecentChats

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), OnUserClickListener, onRecentChatClicked {

    lateinit var rvUsers: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var homeBinding: FragmentHomeBinding
    lateinit var fbauth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentChatAdapter: RecentChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return homeBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)

        userAdapter = UserAdapter()
        rvUsers = view.findViewById(R.id.rvUsers)

        fbauth = FirebaseAuth.getInstance()


        toolbar = view.findViewById(R.id.toolbarMain)
        circleImageView = toolbar.findViewById(R.id.tlImage)

        homeBinding.lifecycleOwner = viewLifecycleOwner

        val LayoutManagerUsers =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvUsers.layoutManager = LayoutManagerUsers

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer {
            userAdapter.setList(it)
            rvUsers.adapter = userAdapter
        })

        userAdapter.setOnClickListener(this)

        homeBinding.logOut.setOnClickListener {

            fbauth.signOut()

            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }

        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(circleImageView)
        })

        recentChatAdapter = RecentChatAdapter()
        userViewModel.getRecentChats().observe(viewLifecycleOwner, Observer {

            homeBinding.rvRecentChats.layoutManager = LinearLayoutManager(activity)

            recentChatAdapter.setOnRecentList(it)
            homeBinding.rvRecentChats.adapter = recentChatAdapter
        })

        recentChatAdapter.setOnRecentChatListener(this)
    }

    override fun onUserSelected(position: Int, users: Users) {

        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(action)
    }

    override fun getOnRecentChatClicked(position: Int, recentchatlist: RecentChats) {
        val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentchatlist)
        view?.findNavController()?.navigate(action)
    }


}