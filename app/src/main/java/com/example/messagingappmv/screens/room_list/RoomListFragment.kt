package com.example.messagingappmv.screens.room_list

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
import androidx.navigation.fragment.findNavController
import com.example.messagingappmv.R
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messagingappmv.MainActivity
import com.example.messagingappmv.database.RoomContactDatabase
import com.example.messagingappmv.databinding.FragmentRoomListBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_contact_list.*
import kotlinx.android.synthetic.main.fragment_contact_list.start_button
import kotlinx.android.synthetic.main.fragment_room_list.*

class RoomListFragment : Fragment() {

    private lateinit var roomListViewModel: RoomListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  super.onCreateView(inflater, container, savedInstanceState)

        // Check if the user is logged in and redirect them if not
        // TODO this should be moved to whichever screen becomes the home screen
        if (TokenStorage.safeLoad(this.context!!) == null)
            this.findNavController().navigate(R.id.action_roomListFragment_to_loginFragment)

//        return view

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRoomListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_room_list, container, false)

        val application = requireNotNull(this.activity).application
        (activity as MainActivity).supportActionBar?.title = getString(R.string.room_list_title)

        // Create an instance of the ViewModel Factory.
        val dataSource = RoomContactDatabase.getInstance(application).roomContactDatabaseDao
        val viewModelFactory = RoomListViewModelFactory(dataSource, application)

        // Get a reference to the ViewModel associated with this fragment.
        roomListViewModel =
            ViewModelProviders.of(
                this, viewModelFactory).get(RoomListViewModel::class.java)

        // To use the View Model with data binding, you have to explicitly
        // give the binding object a reference to it.
        binding.roomListViewModel = roomListViewModel

        val adapter = RoomListAdapter(RoomListListener { room_id ->
            Toast.makeText(context, "${room_id}", Toast.LENGTH_LONG).show()
            roomListViewModel.onRoomContactClicked(room_id)
        })
        binding.roomContactList.adapter = adapter


        roomListViewModel.roomContactList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                room_contact_list.smoothScrollToPosition(0)

            }
        })

        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this)

        // Add an Observer on the state variable for showing a Snackbar message
        // when the CLEAR button is pressed.
        roomListViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                // Reset state to make sure the toast is only shown once, even if the device
                // has a configuration change.
                roomListViewModel.doneShowingSnackbar()
            }
        })

        // Add an Observer on the state variable for Navigating when and item is clicked.
        roomListViewModel.navigateToRoom.observe(this, Observer { id ->
            id?.let {

                this.findNavController().navigate(
                    RoomListFragmentDirections
                        .actionRoomListFragmentToRoomFragment(id))
                roomListViewModel.onRoomNavigated()
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.roomContactList.layoutManager = manager

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        roomListViewModel.attemptAddCurrentWifi(roomListViewModel.getCurrentSsid(this.context!!).toString(), roomListViewModel.getCurrentBssid(this.context!!).toString(), this.context!!)
        start_button.setOnClickListener{
            Log.d("Message", "In Listener")
            roomListViewModel.onSend(roomListViewModel.getCurrentBssid(this.context!!).toString())
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.applicationWindowToken, 0)
            room_contact_list.smoothScrollToPosition(0)
        }
    }
}
