package com.example.messagingappmv.screens.contact_list

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messagingappmv.MainActivity
import com.example.messagingappmv.database.UserDatabase
import com.example.messagingappmv.R
import com.google.android.material.snackbar.Snackbar

import kotlinx.android.synthetic.main.fragment_contact_list.*

import com.example.messagingappmv.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {
    private lateinit var userContactViewModel: ContactListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentContactListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_contact_list, container, false)

        val application = requireNotNull(this.activity).application
        (activity as MainActivity).supportActionBar?.title = getString(R.string.contact_list_title)

        // Create an instance of the ViewModel Factory.
        val dataSource = UserDatabase.getInstance(application).userContactDatabaseDao
        val viewModelFactory = ContactListViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        userContactViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(ContactListViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.contactListViewModel = userContactViewModel

        val adapter = ContactListAdapter(ContactListListener { id ->
            Toast.makeText(context, "${id}", Toast.LENGTH_LONG).show()
            userContactViewModel.onUserContactClicked(id)
        })
        binding.userContactList.adapter = adapter


        userContactViewModel.userContactList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                user_contact_list.smoothScrollToPosition(0)

            }
        })

        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this)

        // Add an Observer on the state variable for showing a Snackbar message
        // when the CLEAR button is pressed.
        userContactViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // Reset state to make sure the toast is only shown once, even if the device
                // has a configuration change.
                userContactViewModel.doneShowingSnackbar()
            }
        })

        // Add an Observer on the state variable for Navigating when and item is clicked.
        userContactViewModel.navigateToChat.observe(this, Observer { night ->
            night?.let {

                this.findNavController().navigate(
                    ContactListFragmentDirections
                        .actionContactListFragmentToChatFragment(night))
                userContactViewModel.onChatNavigated()
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.userContactList.layoutManager = manager

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        start_button.setOnClickListener{
            Log.d("Message", "In Listener")
            userContactViewModel.onSend(editTextMessage.text.toString())
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.applicationWindowToken, 0)
            user_contact_list.smoothScrollToPosition(0)
        }
    }
}

