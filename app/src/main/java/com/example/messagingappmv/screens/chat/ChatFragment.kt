package com.example.messagingappmv.screens.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.messagingappmv.MainActivity
import com.example.messagingappmv.R
import com.example.messagingappmv.database.UserContactDatabase
import com.example.messagingappmv.databinding.FragmentChatBinding


class ChatFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentChatBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chat, container, false)

        val application = requireNotNull(this.activity).application
        (activity as MainActivity).supportActionBar?.title = getString(R.string.chat_title)

        val arguments = ChatFragmentArgs.fromBundle(arguments)

        // Create an instance of the ViewModel Factory.
        val dataSource = UserContactDatabase.getInstance(application).userContactDatabaseDao
        val viewModelFactory = ChatViewModelFactory(arguments.userContactKey, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        val chatViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(ChatViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.chatViewModel = chatViewModel

        binding.setLifecycleOwner(this)

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        chatViewModel.navigateToContactList.observe(this, Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    ChatFragmentDirections.actionChatFragmentToContactListFragment())
                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                chatViewModel.doneNavigating()
            }
        })

        return binding.root
    }
}
