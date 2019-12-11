package com.example.messagingappmv.screens.room

/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messagingappmv.database.AppRoomPosts
import com.example.messagingappmv.database.AppRoomPostsDatabaseDao
import kotlinx.coroutines.*

/**
 * ViewModel for SleepQualityFragment.
 *
 * @param sleepNightKey The key of the current night we are working on.
 */
class AppRoomViewModel(
    private val roomKey: Long = 0L,
    dataSource: AppRoomPostsDatabaseDao
) : ViewModel() {

    val database = dataSource

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()

//    private val newRoomContact: LiveData<AppRoomPosts>
//
//    fun getNewRoomContact() = newRoomContact
//
//
//    init {
//        newRoomContact=database.getRoomPostsById(roomContactKey)
//    }

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var newRoomPosts = MutableLiveData<AppRoomPosts?>()

    val allRoomPosts = database.getAllRoomPosts()

    /**
     * Request a toast by setting this value to true.
     *
     * This is private because we don't want to expose setting this value to the Fragment.
     */
    private var _showSnackbarEvent = MutableLiveData<Boolean?>()

    /**
     * If this is true, immediately `show()` a toast and call `doneShowingSnackbar()`.
     */
    val showSnackBarEvent: LiveData<Boolean?>
        get() = _showSnackbarEvent


    /**
     * Call this immediately after calling `show()` on a toast.
     *
     * It will clear the toast request, so if the appRoom rotates their phone it won't show a duplicate
     * toast.
     */
    fun doneShowingSnackbar() {
        _showSnackbarEvent.value = null
    }


    /**
     * Navigation for the AppRoom fragment.
     */
    private val _navigateToRoom = MutableLiveData<Long>()
    val navigateToRoom
        get() = _navigateToRoom

    fun onRoomContactClicked(id: Long) {
        _navigateToRoom.value = id
    }

    fun onRoomNavigated() {
        _navigateToRoom.value = null
    }

    init {
        initializeRoomContact()
    }

    private fun initializeRoomContact() {
        uiScope.launch {
            newRoomPosts.value = getRoomPostFromDatabase()
        }
    }

    /**
     *  Handling the case of the stopped app or forgotten recording,
     *  the start and end times will be the same.j
     *
     *  If the start time and end time are not the same, then we do not have an unfinished
     *  recording.
     */
    private suspend fun getRoomPostFromDatabase(): AppRoomPosts? {
        return withContext(Dispatchers.IO) {

            var roomPost = database.getRoomPosts()
            if (roomPost?.id == roomPost?.id) {
                roomPost = null
            }
            roomPost
        }
    }

    private suspend fun insert(appRoomPost: AppRoomPosts) {
        withContext(Dispatchers.IO) {
            database.insert(appRoomPost)
        }
    }

    private suspend fun update(appRoomPost: AppRoomPosts) {
        withContext(Dispatchers.IO) {
            database.update(appRoomPost)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    /**
     * Executes when the START button is clicked.
     */
    fun onStart() {
        uiScope.launch {
            // Create a new night, which captures the current time,
            // and insert it into the database.
            val newPost = AppRoomPosts()
            newPost.uid = 8

            insert(newPost)

            newRoomPosts.value = getRoomPostFromDatabase()
        }
    }

    fun onSend(post: String) {
        uiScope.launch {
            val newPost = AppRoomPosts()
            newPost.uid = 8
            newPost.message = post

            Log.d("Post", post)
            insert(newPost)
            newRoomPosts.value = getRoomPostFromDatabase()



        }
    }

    /**
     * Executes when the CLEAR button is clicked.
     */
    fun onClear() {
        uiScope.launch {
            // Clear the database table.
            clear()

            // And clear tonight since it's no longer in the database
            newRoomPosts.value = null

            // Show a snackbar post, because it's friendly.
            _showSnackbarEvent.value = true
        }
    }

    /**
     * Called when the ViewModel is dismantled.
     * At this point, we want to cancel all coroutines;
     * otherwise we end up with processes that have nowhere to return to
     * using memory and resources.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}