package com.example.messagingappmv.webservices.firebase

object FirebaseEventManager {

    private val dmListeners = ArrayList<FirebaseDMEventListener>()

    fun addDMListener(listener: FirebaseDMEventListener) {
        dmListeners.add(listener)
    }

    fun removeDMListener(listener: FirebaseDMEventListener) {
        val iterator = dmListeners.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if(it == listener) {
                iterator.remove()
                break
            }
        }
    }

    fun publishDMEvent(uid: Long) {
        dmListeners.forEach { listener ->
            listener.onFirebaseDMEvent(uid)
        }
    }
}