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
import kotlinx.android.synthetic.main.fragment_room_list.start_button
import kotlinx.android.synthetic.main.fragment_room_list.*

class RoomListFragment : Fragment() {
    private lateinit var roomListViewModel: RoomListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Check if the user is logged in and redirect them if not
        if (!TokenStorage.containsToken(this.context!!))
            this.findNavController().navigate(R.id.action_roomListFragment_to_loginFragment)

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentRoomListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_room_list, container, false)

        val application = requireNotNull(this.activity).application

        //Set Title of ActionBar
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
        val currentWifi = roomListViewModel.getCurrentWifi(context!!)
        val adapter = RoomListAdapter(RoomListListener { id ->
            Toast.makeText(context, "${id}", Toast.LENGTH_LONG).show()
            roomListViewModel.onRoomContactClicked(id)
        }, currentWifi)
        binding.roomContactList.adapter = adapter


        roomListViewModel.roomContactList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.addHeaderAndSubmitList(it)
                room_contact_list.smoothScrollToPosition(0)
            }
        })

        // Specify the current activity as the lifecycle owner of the binding.
        // This is necessary so that the binding can observe LiveData updates.
        binding.setLifecycleOwner(this)

        // Add an Observer on the state variable for Navigating when and item is clicked.
        roomListViewModel.navigateToRoom.observe(this, Observer { data ->
            if(data != null) {
                id?.let {

                    this.findNavController().navigate(
                        RoomListFragmentDirections
                            .actionRoomListFragmentToRoomFragment(data)
                    )
                    roomListViewModel.onRoomNavigated()
                }
            }
        })

        val manager = LinearLayoutManager(activity)
        binding.roomContactList.layoutManager = manager

        return binding.root
    }

override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    roomListViewModel.addPublicWifi(this.context!!)
}
}