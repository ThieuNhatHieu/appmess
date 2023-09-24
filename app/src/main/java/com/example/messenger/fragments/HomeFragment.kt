package com.example.messenger.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.example.messenger.R
import com.example.messenger.SignUpActivity
import com.example.messenger.adapter.OnUserClickListener
import com.example.messenger.adapter.UserAdapter
import com.example.messenger.databinding.FragmentHomeBinding
import com.example.messenger.modal.Users
import com.example.messenger.mvvm.ChatAppViewModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import androidx.appcompat.widget.Toolbar
import com.example.messenger.SignInActivity

@Suppress("DEPRECATION")
class HomeFragment : Fragment(), OnUserClickListener {

    lateinit var rvUsers: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var homeBinding: FragmentHomeBinding
    lateinit var fbauth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView

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
            userAdapter.setOnClickListener(this)
            rvUsers.adapter = userAdapter
        })

        homeBinding.logOut.setOnClickListener {

            fbauth.signOut()

            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }

        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext()).load(it).into(circleImageView)
        })
    }

    override fun onUserSelected(position: Int, users: Users) {

    }


}