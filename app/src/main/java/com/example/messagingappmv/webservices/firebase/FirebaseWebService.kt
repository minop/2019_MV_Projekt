package com.example.messagingappmv.webservices.firebase

import android.content.Context
import android.util.Log
import com.example.messagingappmv.webservices.WebserviceTask
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.firebase.requestbodies.MessageData
import com.example.messagingappmv.webservices.firebase.requestbodies.MessageRequest
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object FirebaseWebService {

    private const val BASE_URL = "https://fcm.googleapis.com/fcm/send/"
    private val service: FirebaseWebServiceInterface

    init {
        val httpClient = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(httpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

        service = retrofit.create(FirebaseWebServiceInterface::class.java)
    }

    fun notifyUser(recieverFID: String, sender: String, message: String) {
        WebserviceTask<Unit>({}).execute(
            service.sendMessage(MessageRequest(recieverFID, MessageData(sender, message)))
        )
    }

    fun notifyRoom(roomSSID: String, sender: String, message: String) {
        WebserviceTask<Unit>({}).execute(
            service.sendMessage(MessageRequest("/topics/$roomSSID", MessageData(sender, message)))
        )
    }

    fun subscribeToRoom(roomSSID: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(roomSSID)
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
}