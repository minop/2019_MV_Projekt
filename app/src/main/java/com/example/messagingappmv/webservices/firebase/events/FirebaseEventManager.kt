package com.example.messagingappmv.webservices.firebase.events

object FirebaseEventManager {

    private val dmListeners = ArrayList<FirebaseDMEventListener>()
    private val roomListeners = ArrayList<FirebaseRoomEventListener>()

    fun addDMListener(listener: FirebaseDMEventListener) {
        dmListeners.add(listener)
    }

    fun publishDMEvent(uid: Long) {
        dmListeners.forEach { listener ->
            listener.onFirebaseDMEvent(uid)
        }
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

    fun addRoomListener(listener: FirebaseRoomEventListener) {
        roomListeners.add(listener)
    }

    fun publishRoomEvent(ssid: String) {
        roomListeners.forEach{ listener ->
            listener.onFirebaseRoomEvent(ssid)
        }
    }

    fun removeRoomListener(listener: FirebaseRoomEventListener) {
        val iterator = roomListeners.iterator()
        while (iterator.hasNext()) {
            val it = iterator.next()
            if(it == listener) {
                iterator.remove()
                break
            }
        }
    }
}