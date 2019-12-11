package com.example.messagingappmv.webservices.firebase

import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Firebase : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
       if(TokenStorage.containsToken(this.baseContext))
            CavojskyWebService.updateFirebaseToken(token, this.baseContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if(message.data.isNotEmpty()) {
            if(message.data.get("type").equals(FirebaseWebService.TYPE_USER)) {
                FirebaseEventManager.publishEvent(message.data.get("identifier")!!.toLong())
            }
        }
    }
}