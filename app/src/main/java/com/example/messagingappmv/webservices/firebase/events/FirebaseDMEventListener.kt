package com.example.messagingappmv.webservices.firebase.events

interface FirebaseDMEventListener {
    fun onFirebaseDMEvent(senderUID: Long)
}