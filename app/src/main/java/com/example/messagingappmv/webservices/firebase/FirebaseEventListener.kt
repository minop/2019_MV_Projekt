package com.example.messagingappmv.webservices.firebase

interface FirebaseEventListener {
    fun onFirebaseEvent(senderUID: Long)
}