package com.example.messagingappmv.webservices.firebase

import com.example.messagingappmv.webservices.firebase.requestbodies.MessageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FirebaseWebServiceInterface {
    @POST
    @Headers("Authorization: key=AIzaSyAKD0XerNvC7D6SfSvN6je4UaasUdfClIc")
    fun sendMessage(@Body body: MessageRequest) : Call<Unit>
}