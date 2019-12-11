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
import kotlinx.coroutines.*

/**
 * ViewModel for SleepQualityFragment.
 *
 * @param sleepNightKey The key of the current night we are working on.
 */
class RoomViewModel(
    private val roomContactKey: Long = 0L,
    dataSource: UserPostsDatabaseDao,
    application: Application

) : ViewModel() {
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
    val allUserPosts = database.getAllUserPosts(uid, roomContactKey)
    
    /**
     * Navigation for the Chat fragment.
     */
    private val _navigateToRoom = MutableLiveData<Long>()

    fun onRoomContactClicked(id: Long) {
        _navigateToRoom.value = id
    }

    init {
        initializeRoomContact()
    }

    private fun initializeRoomContact() {
        uiScope.launch {
            newUserPosts.value = getUserPostsFromDatabase()
        }
    }
    
    private suspend fun getUserPostsFromDatabase(): UserPosts? {
        val userMessages = mutableListOf<UserPosts>()

        Log.d("Uid Login user", TokenStorage.load(context).uid)
        Log.d("Room Uid", roomContactKey.toString())
        var numMessages = -1

        CavojskyWebService.getRoomMessages(roomContactKey.toString(), context, { posts ->
            Log.d("ContactListItem", posts.toString())
            for (item: RoomReadItem in posts) {
                val tmpRoomContact = UserPosts(
                    item.uid.toLong(),
                    item.contact.toLong(),
                    item.message,
                    item.time,
                    item.uid_name,
                    item.contact_name
                )
                userMessages.add(tmpRoomContact)
                numMessages = allUserMessages.value?.size ?: -1
                Log.d("All posts length", numMessages.toString())
            }

            if (numMessages < userMessages.size && numMessages != -1) {
                AsyncTask.execute {
                    database.insertAll(
                        userMessages.subList(
                            numMessages,
                            userMessages.size
                        )
                    )
                }
                Toast.makeText(context, "New Messages", Toast.LENGTH_LONG).show()

            } else {
                Toast.makeText(context, "Nothing new", Toast.LENGTH_LONG).show()

            }
            Log.d("Messages", userMessages.toString())
        })
        return withContext(Dispatchers.IO) {

            var userContact = database.getUserMessage()
            if (userContact?.uid_name == userContact?.uid_name) {
                userContact = null
            }
            userContact
        }
    }

    fun onSend(post: String) {
        uiScope.launch {
            val newPost = UserPosts()
            newPost.uid = 8
            newPost.post = post

            CavojskyWebService.sendMessageToContact(
                roomContactKey.toString(),
                message,
                context, {
                    AsyncTask.execute {
                        database.insert(newMessage)
                    }
                })
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