package com.example.messagingappmv.webservices.firebase

import android.content.Context
import android.util.Log
import com.example.messagingappmv.webservices.WebserviceTask
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.firebase.requestbodies.MessageData
import com.example.messagingappmv.webservices.firebase.requestbodies.MessageRequest
import com.example.messagingappmv.webservices.firebase.requestbodies.NotificationData
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern


object FirebaseWebService {

    private const val BASE_URL = "https://fcm.googleapis.com"
    private val service: FirebaseWebServiceInterface

    private const val TYPE_USER = "user"
    private const val TYPE_ROOM = "room"

    private val cleansingRegex = Regex("[^a-zA-Z0-9-_.~%]")

    init {
        val httpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

        service = retrofit.create(FirebaseWebServiceInterface::class.java)
    }

    fun notifyUser(recieverFID: String, senderID: String, sender: String, message: String) {
        WebserviceTask<Unit>({}).execute(
            service.sendMessage(MessageRequest(recieverFID, MessageData(TYPE_USER, senderID), NotificationData(sender, message)))
        )
    }

    fun notifyRoom(roomSSID: String, sender: String, message: String) {
        WebserviceTask<Unit>({}).execute(
            service.sendMessage(MessageRequest("/topics/${cleanseSSID(roomSSID)}", MessageData(TYPE_ROOM, roomSSID), NotificationData(sender, message)))
        )
    }

    fun subscribeToRoom(roomSSID: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(cleanseSSID(roomSSID))
    }

    fun forceTokenRegistration(context: Context) {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if(!task.isSuccessful) {
                Log.e("Firebase", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }

            CavojskyWebService.updateFirebaseToken(task.result!!.token, context)
        }
    }

    private fun cleanseSSID(ssid: String): String {
        return cleansingRegex.replace(ssid, "_")
    }
}