package com.example.messagingappmv.screens.room_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.messagingappmv.R
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage

class RoomListFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)

        // Check if the user is logged in and redirect them if not
        if (!TokenStorage.containsToken(this.context!!))
            this.findNavController().navigate(R.id.action_roomListFragment_to_loginFragment)

        return view
    }
}