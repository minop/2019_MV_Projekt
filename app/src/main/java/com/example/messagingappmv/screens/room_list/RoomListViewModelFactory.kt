package com.example.messagingappmv.screens.room_list

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messagingappmv.database.RoomContactDatabaseDao
/**
 * This is pretty much boiler plate code for a ViewModel Factory.
 *
 * Provides the UserContactDatabaseDao and context to the ViewModel.
 */
class RoomListViewModelFactory(
    private val dataSource: RoomContactDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomListViewModel::class.java)) {
            return RoomListViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
