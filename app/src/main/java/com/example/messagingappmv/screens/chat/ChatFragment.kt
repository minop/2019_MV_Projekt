package com.example.messagingappmv.screens.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messagingappmv.MainActivity
import com.example.messagingappmv.R
import com.example.messagingappmv.database.UserContactDatabase
import com.example.messagingappmv.databinding.FragmentChatBinding
import com.example.messagingappmv.screens.contact_list.ContactListAdapter
import com.example.messagingappmv.screens.contact_list.ContactListListener
import com.example.messagingappmv.screens.contact_list.ContactListViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_contact_list.*
import kotlinx.android.synthetic.main.fragment_contact_list.user_contact_list


class ChatFragment : Fragment() {
    private lateinit var userMessagesViewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val uid = 326L
        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentChatBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chat, container, false)

        val application = requireNotNull(this.activity).application
        (activity as MainActivity).supportActionBar?.title = getString(R.string.chat_title)

        val arguments = ChatFragmentArgs.fromBundle(arguments)

        // Create an instance of the ViewModel Factory.
        val dataSource = UserContactDatabase.getInstance(application).userMessagesDatabaseDao
        val viewModelFactory = ChatViewModelFactory(arguments.userContactKey, uid, dataSource)

        // Get a reference to the ViewModel associated with this fragment.
        userMessagesViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(ChatViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.chatViewModel = userMessagesViewModel

        val adapter = ChatAdapter(uid, arguments.userContactKey ,ChatListener { id ->
            Toast.makeText(context, "${id}", Toast.LENGTH_LONG).show()
            userMessagesViewModel.onUserContactClicked(id)
        })
        binding.userMessages.adapter = adapter


        userMessagesViewModel.allUserMessages.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
                user_messages.smoothScrollToPosition(0)

            }
        })

        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this)

        // Add an Observer on the state variable for showing a Snackbar message
        // when the CLEAR button is pressed.
        userMessagesViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // Reset state to make sure the toast is only shown once, even if the device
                // has a configuration change.
                userMessagesViewModel.doneShowingSnackbar()
            }
        })


        val manager = LinearLayoutManager(activity)
        manager.setReverseLayout(true)

        binding.userMessages.layoutManager = manager
        return binding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        send_button.setOnClickListener{
            Log.d("Message", "In Listener")
            userMessagesViewModel.onSend(editTextMessageChat.text.toString())

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.applicationWindowToken, 0)


        }
        editTextMessageChat.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    user_messages.smoothScrollToPosition(0)

                }

                override fun beforeTextChanged(s: CharSequence, start: Int,
                                               count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int,
                                           before: Int, count: Int) {
                    user_messages.smoothScrollToPosition(0)
                }
            }
        )
    }
}
