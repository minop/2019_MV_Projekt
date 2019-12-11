package com.example.messagingappmv.webservices.firebase

interface FirebaseDMEventListener {
    fun onFirebaseDMEvent(senderUID: Long)
}