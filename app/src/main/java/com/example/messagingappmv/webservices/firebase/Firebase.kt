package com.example.messagingappmv.webservices.firebase

import android.util.Log
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.google.firebase.messaging.FirebaseMessagingService

object Firebase : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.d("Firebase", "Refreshed token: $token")
        CavojskyWebService.updateFirebaseToken(token, this.baseContext)
    }
}