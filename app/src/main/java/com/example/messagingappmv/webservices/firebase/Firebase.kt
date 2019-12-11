package com.example.messagingappmv.webservices.firebase

import android.util.Log
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Firebase : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("Firebase", "Refreshed token: $token")
        CavojskyWebService.updateFirebaseToken(token, this.baseContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("", "From: ${message.from}")

        // Check if message contains a data payload.
        message.data.isNotEmpty().let {
            Log.d("", "Message data payload: " + message.data)
        }

        // Check if message contains a notification payload.
        message.notification?.let {
            Log.d("", "Message Notification Body: ${it.body}")
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}