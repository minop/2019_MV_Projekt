package com.example.messagingappmv.webservices.firebase

object FirebaseEventManager {

    private val listeners = ArrayList<((Long)-> Unit)?>()

    fun subscribe(listener: (Long) -> Unit) {
        listeners.add { listener }
    }

    fun publishEvent(uid: Long) {
        listeners.forEach {listener ->
            listener?.invoke(uid)
        }
    }
}