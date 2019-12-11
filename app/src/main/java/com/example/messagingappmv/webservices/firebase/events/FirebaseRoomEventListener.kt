package com.example.messagingappmv.webservices.firebase.events

interface FirebaseRoomEventListener {
    fun onFirebaseRoomEvent(roomSSID: String)
}