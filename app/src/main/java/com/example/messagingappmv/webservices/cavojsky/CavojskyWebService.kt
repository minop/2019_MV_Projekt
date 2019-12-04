package com.example.messagingappmv.webservices.cavojsky

import android.content.Context
import com.example.messagingappmv.webservices.cavojsky.interceptors.AuthInterceptor
import com.example.messagingappmv.webservices.cavojsky.interceptors.LoginData
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenAuthenticator
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.example.messagingappmv.webservices.cavojsky.requestbodies.LoginRequest
import com.example.messagingappmv.webservices.cavojsky.requestbodies.RefreshRequest
import com.example.messagingappmv.webservices.cavojsky.requestbodies.RegistrationRequest
import com.example.messagingappmv.webservices.cavojsky.requestbodies.RoomListRequest
import com.example.messagingappmv.webservices.cavojsky.responsebodies.LoginResponse
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RefreshResponse
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RegistrationResponse
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RoomListItem
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalStateException

object CavojskyWebService {

    private const val BASE_URL = "http://zadanie.mpage.sk"

    private fun create(context: Context) : CavojskyWebServiceInterface {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .authenticator(TokenAuthenticator(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CavojskyWebServiceInterface::class.java)
    }

    private fun makeException(error: String) : IllegalStateException {
        return IllegalStateException("webservice call failed: $error")
    }

    // webservice operations
    fun register(username: String, password: String, context: Context) {
        WebserviceTask<RegistrationResponse> { response ->
            TokenStorage.save(LoginData(response.uid, response.access, response.refresh), context)
        }.execute(
            this.create(context).register(RegistrationRequest(username, password))
        )
    }

    fun login(username: String, password: String, context: Context) {
        WebserviceTask<LoginResponse> { response ->
            TokenStorage.save(LoginData(response.uid, response.access, response.refresh), context)
        }.execute(
            this.create(context).login(LoginRequest(username, password))
        )
    }

    fun refreshTokens(uid: String, refreshToken: String, context: Context) : RefreshResponse {
        val response = this.create(context).refreshTokens(RefreshRequest(uid, refreshToken)).execute()

        if(response.isSuccessful)
            return response.body()!!

        throw makeException(response.errorBody()?.string()!!)
    }

    fun listRooms(context: Context, callback: (List<RoomListItem>) -> Any) {
        WebserviceTask(callback).execute(
            this.create(context).getRooms(RoomListRequest( TokenStorage.load(context).uid ))
        )
    }
}