package com.example.messagingappmv.screens.chat

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
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.messagingappmv.database.UserMessages
import com.example.messagingappmv.database.UserMessagesDatabaseDao
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.example.messagingappmv.webservices.cavojsky.responsebodies.ContactReadItem
import com.example.messagingappmv.webservices.firebase.FirebaseEventManager
import com.example.messagingappmv.webservices.firebase.FirebaseWebService
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.*

/**
 * ViewModel for ChatFragment.
 *
 * @param userContactKey The key of the current chat we are working on.
 */
class ChatViewModel(
    private val userContactKey: Long,
    dataSource: UserMessagesDatabaseDao,
    application: Application

) : ViewModel() {
    private val context = application.applicationContext
    private val uid: Long = TokenStorage.load(context).uid.toLong()
    val database = dataSource


    private lateinit var contactFID: String
    private lateinit var myUsername: String

    /** Coroutine setup variables */

    /**
     * viewModelJob allows us to cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _newUserMessages = MutableLiveData<UserMessages?>()
    val allUserMessages = database.getAllUserMessages(uid, userContactKey)

    /**
     * Navigation for the Chat fragment.
     */
    private val _navigateToChat = MutableLiveData<Long>()

    fun onUserContactClicked(id: Long) {
        _navigateToChat.value = id
    }

    init {
        initializeUserContact()
    }

    private fun initializeUserContact() {
        uiScope.launch {
            _newUserMessages.value = getUserMessagesFromDatabase(
                userContactKey,
                context,
                allUserMessages,
                database
            ) { message ->
                if (message.uid == uid.toString()) {
                    contactFID = message.contact_fid
                    myUsername = message.uid_name
                } else {
                    contactFID = message.uid_fid
                    myUsername = message.contact_name
                }
            }
        }

        FirebaseEventManager.subscribe { uid_fid ->
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    getUserMessagesFromDatabase(
                        userContactKey,
                        context,
                        allUserMessages,
                        database
                    )
                }
            }
        }
    }

    suspend fun getUserMessagesFromDatabase(
        userContactKey: Long,
        context: Context,
        allUserMessages: LiveData<List<UserMessages>>,
        database: UserMessagesDatabaseDao,
        fillMetadataCallback: (ContactReadItem) -> Unit = {}
    ): UserMessages? {
        val userMessages = mutableListOf<UserMessages>()

        Log.d("Uid Login user", TokenStorage.load(context).uid)
        Log.d("Contact Uid", userContactKey.toString())
        var numMessages = -1

        CavojskyWebService.getContactMessages(userContactKey.toString(), context, { messages ->
            Log.d("ContactListItem", messages.toString())
            for (item: ContactReadItem in messages) {
                val tmpUserContact = UserMessages(
                    item.uid.toLong(),
                    item.contact.toLong(),
                    item.message,
                    item.time,
                    item.uid_name,
                    item.contact_name
                )
                userMessages.add(tmpUserContact)
                numMessages = allUserMessages.value?.size ?: -1
                Log.d("All messages length", numMessages.toString())
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

    fun onSend(message: String) {
        uiScope.launch {
            val newMessage = UserMessages()
            newMessage.uid = uid
            newMessage.message = message
            newMessage.contact_id = userContactKey

            CavojskyWebService.sendMessageToContact(
                userContactKey.toString(),
                message,
                context, {
                    AsyncTask.execute {
                        database.insert(newMessage)
                    }
                })
            FirebaseWebService.notifyUser(contactFID, uid.toString(), myUsername, message)

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