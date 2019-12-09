package com.example.messagingappmv.screens.chat

import android.content.Context
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingappmv.MainActivity
import com.example.messagingappmv.R
import com.example.messagingappmv.database.UserDatabase
import com.example.messagingappmv.databinding.FragmentChatBinding
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.giphy.sdk.core.models.Media
import com.giphy.sdk.ui.GPHSettings
import com.giphy.sdk.ui.GiphyCoreUI
import com.giphy.sdk.ui.themes.GridType
import com.giphy.sdk.ui.themes.LightTheme
import com.giphy.sdk.ui.views.GiphyDialogFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {
    private lateinit var userMessagesViewModel: ChatViewModel
    private lateinit var manager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val uid = context?.let { TokenStorage.load(it).uid }!!.toLong()

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentChatBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_chat, container, false
        )

        val application = requireNotNull(this.activity).application
        (activity as MainActivity).supportActionBar?.title = getString(R.string.chat_title)

        val arguments = ChatFragmentArgs.fromBundle(arguments)
        Log.d("ContactUid", arguments.toString())

        // Create an instance of the ViewModel Factory.
        val dataSource = UserDatabase.getInstance(application).userMessagesDatabaseDao
        val viewModelFactory =
            ChatViewModelFactory(arguments.userContactKey, dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        userMessagesViewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(ChatViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.chatViewModel = userMessagesViewModel

        val adapter = ChatAdapter(uid, arguments.userContactKey, ChatListener { id ->
            Toast.makeText(context, "${id}", Toast.LENGTH_LONG).show()
            userMessagesViewModel.onUserContactClicked(id)
        })
        binding.userMessages.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                user_messages.scrollToPosition(0)
            }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                user_messages.scrollToPosition(0)
            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                user_messages.scrollToPosition(0)
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                user_messages.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                user_messages.scrollToPosition(0)
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                user_messages.scrollToPosition(0)
            }
        })


        userMessagesViewModel.allUserMessages.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
//                adapter.notifyItemRangeChanged(0, it.size);

//                adapter.notifyItemInserted()
//                adapter.notification.observe(this, Observer {
//                    user_messages.smoothScrollToPosition(0)
//                })



            Toast.makeText(context, R.string.added_new_message, Toast.LENGTH_LONG).show()

        }
    })

    // Specify the current activity as the lifecycle owner of the binding.
    // This is necessary so that the binding can observe LiveData updates.
    binding.setLifecycleOwner(this)

    // Add an Observer on the state variable for showing a Snackbar message
    // when the CLEAR button is pressed.
    userMessagesViewModel.showSnackBarEvent.observe(this, Observer
    {
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


    userMessagesViewModel.allUserMessages.observe(this, Observer
    {


    })

//        userMessagesViewModel.newMessageNotification.observe(this, Observer {
//            if (it == true) { // Observed state is true.
//                // Reset state to make sure the toast is only shown once, even if the device
//                // has a configuration change.
//
//                userMessagesViewModel.doneNewMessageNotification()
//            }
//        })


    manager = LinearLayoutManager(activity)
//        manager.setStackFromEnd(true)
    manager.reverseLayout = true


    binding.userMessages.layoutManager = manager
//        manager.smoothScrollToPosition(binding.userMessages, null, 0)

    return binding.root
}

override fun onActivityCreated(savedInstanceState: Bundle?) {

    super.onActivityCreated(savedInstanceState)
    context?.let {
        GiphyCoreUI.configure(
            context = it,
            apiKey = "TMyIPe2V232AT5Y3LF2GtyFf1qrpBzSf",
            verificationMode = false
        )
    }

    send_button.setOnClickListener {
        Log.d("Message", "In Listener")
        val newMessage = editTextMessageChat.text.toString()
        if (newMessage != "") {
            userMessagesViewModel.onSend(newMessage)
            editTextMessageChat.text.clear()
        } else {
            Snackbar.make(
                activity!!.findViewById(android.R.id.content),
                getString(R.string.empty_message),
                Snackbar.LENGTH_SHORT
            ).show()
        }

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.applicationWindowToken, 0)


    }

    send_gif_button.setOnClickListener {
        val settings =
            GPHSettings(gridType = GridType.waterfall, theme = LightTheme, dimBackground = true)
        val gifsDialog = GiphyDialogFragment.newInstance(settings)
        fragmentManager?.let { gifsDialog.show(it, "gifs_dialog") }
        gifsDialog.gifSelectionListener = object : GiphyDialogFragment.GifSelectionListener {
            override fun onGifSelected(media: Media) {
                Log.d("ID Gif", media.id)
                val newMessage = "gif:" + media.id
                userMessagesViewModel.onSend(newMessage)
            }

            override fun onDismissed() {
                //Your user dismissed the dialog without selecting a GIF
            }
        }

    }

//        editTextMessageChat.addTextChangedListener(
//            object : TextWatcher {
//
//                override fun afterTextChanged(s: Editable) {
//                    user_messages.smoothScrollToPosition(0)
//
//                }
//
//                override fun beforeTextChanged(s: CharSequence, start: Int,
//                                               count: Int, after: Int) {
//                }
//
//                override fun onTextChanged(s: CharSequence, start: Int,
//                                           before: Int, count: Int) {
//                    user_messages.smoothScrollToPosition(0)
//                }
//            }
//        )
}
}
