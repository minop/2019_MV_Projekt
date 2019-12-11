package com.example.messagingappmv.webservices.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
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

    fun notifyUser(recieverFID: String) {
        FirebaseMessaging.getInstance().send(
            RemoteMessage.Builder("eBvxkWv0K8I:APA91bEvolDMa9_nIfx3eEgU_6zpGmq2rfNAcV9ZHcTG_5YCnDNyO9aqlhnY4APu-wI1kMN0lagmFwqRAJOUxS5MRTjMqdDDO2MQ-DuhyPIdP7SCX9XfafR0JjxHmuiMBM1DEMK5zwba" + "@gcm.googleapis.com")
                .setMessageId("msgID")
                .addData("key", "value")
                .build()
        )
    }
}