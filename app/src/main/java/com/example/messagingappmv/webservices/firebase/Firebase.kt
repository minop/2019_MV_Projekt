package com.example.messagingappmv.webservices.firebase

import com.example.messagingappmv.database.UserDatabase
import com.example.messagingappmv.screens.chat.ChatViewModel
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Firebase : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
       if(TokenStorage.containsToken(this.baseContext))
            CavojskyWebService.updateFirebaseToken(token, this.baseContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if(message.data.isNotEmpty()) {
            if(message.data.get("type").equals(FirebaseWebService.TYPE_USER)) {

                val uid = TokenStorage.load(this.baseContext).uid.toLong()

                val userContactKey = message.data.get("identifier")!!.toLong()
                val context = this.baseContext
                val database = UserDatabase.getInstance(application).userMessagesDatabaseDao

                val allUserMessages = database.getAllUserMessages(uid, userContactKey)

                GlobalScope.launch {
                    ChatViewModel.getUserMessagesFromDatabase(
                        userContactKey,
                        context,
                        allUserMessages,
                        database
                    )
                }
            }
        }
    }
}