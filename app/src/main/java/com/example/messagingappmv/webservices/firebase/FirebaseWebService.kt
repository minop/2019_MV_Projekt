package com.example.messagingappmv.webservices.firebase

import com.example.messagingappmv.webservices.WebserviceTask
import com.example.messagingappmv.webservices.firebase.requestbodies.MessageData
import com.example.messagingappmv.webservices.firebase.requestbodies.MessageRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object FirebaseWebService {

    private const val BASE_URL = "https://fcm.googleapis.com/fcm/send"
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
}