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

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messagingappmv.database.UserContact
import com.example.messagingappmv.database.UserPosts
import com.example.messagingappmv.database.UserPostsDatabaseDao
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RoomReadItem
import com.example.messagingappmv.webservices.firebase.FirebaseWebService
import com.example.messagingappmv.webservices.firebase.events.FirebaseEventManager
import com.example.messagingappmv.webservices.firebase.events.FirebaseRoomEventListener
import kotlinx.coroutines.*

/**
 * ViewModel for SleepQualityFragment.
 *
 * @param sleepNightKey The key of the current night we are working on.
 */
class RoomViewModel(
    private val roomContactKey: String = "",
    dataSource: UserPostsDatabaseDao,
    application: Application

) : ViewModel(), FirebaseRoomEventListener {
    override fun onFirebaseRoomEvent(roomSSID: String) {
        uiScope.launch {
            if(roomSSID == roomContactKey)
                getUserPostsFromDatabase()
        }
    }

    private val context = application.applicationContext
    private val uid: Long = TokenStorage.load(context).uid.toLong()
    val database = dataSource

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var newUserPosts = MutableLiveData<UserPosts?>()
    val allUserPosts = database.getAllPostsFromRoom(roomContactKey)

    /**
     * Navigation for the Chat fragment.
     */
    private val _navigateToRoom = MutableLiveData<String>()

    fun onRoomContactClicked(id: String) {
        _navigateToRoom.value = id

    }

    init {
        initializeRoomContact()

        FirebaseEventManager.addRoomListener(this)
    }

    private fun initializeRoomContact() {
        uiScope.launch {
            newUserPosts.value = getUserPostsFromDatabase()
        }
    }

    private suspend fun getUserPostsFromDatabase(): UserPosts? {
        val userPosts = mutableListOf<UserPosts>()

        Log.d("Uid Login user", TokenStorage.load(context).uid)
        Log.d("Room ssid", roomContactKey)
        var numMessages = -1

        CavojskyWebService.getRoomMessages(roomContactKey, context, { posts ->
            Log.d("ContactListItem", posts.toString())
            for (item: RoomReadItem in posts) {
                val tmpRoomContact = UserPosts(
                    item.uid.toLong(),
                    item.roomid,
                    item.message
                )
                userPosts.add(tmpRoomContact)
                numMessages = allUserPosts.value?.size ?: -1
                Log.d("All posts length", numMessages.toString())
            }

            if (numMessages < userPosts.size && numMessages != -1) {
                AsyncTask.execute {
                    database.insertAll(
                        userPosts.subList(
                            numMessages,
                            userPosts.size
                        )
                    )
                }
                Toast.makeText(context, "New Posts", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(context, "Nothing new", Toast.LENGTH_LONG).show()

            }
            Log.d("Posts", userPosts.toString())
        })
        return withContext(Dispatchers.IO) {

            var roomContact = database.getUserPost()
            if (roomContact?.room_id == roomContact?.room_id) {
                roomContact = null
            }
            roomContact
        }
    }

    fun onSend(post: String) {
        uiScope.launch {
            FirebaseWebService.subscribeToRoom(roomContactKey)

            val newPost = UserPosts()
            newPost.uid = uid
            newPost.post = post
            newPost.room_id = roomContactKey

            CavojskyWebService.sendMessageToRoom(
                roomContactKey,
                post,
                context, {
                    AsyncTask.execute {
                        database.insert(newPost)
                    }
                })
            FirebaseWebService.notifyRoom(roomContactKey, uid.toString(), post)

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