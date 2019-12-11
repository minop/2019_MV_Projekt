package com.example.messagingappmv.webservices.firebase

object FirebaseEventManager {

    private val listeners = ArrayList<FirebaseEventListener?>()

    fun addListener(listener: FirebaseEventListener) {
        listeners.add(listener)
    }

    fun publishEvent(uid: Long) {
        listeners.forEach {listener ->
            listener?.onFirebaseEvent(uid)
        }
    }
}