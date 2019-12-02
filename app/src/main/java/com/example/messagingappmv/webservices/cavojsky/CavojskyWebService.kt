package com.example.messagingappmv.webservices.cavojsky

import android.content.Context
import com.example.messagingappmv.webservices.cavojsky.interceptors.AuthInterceptor
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenAuthenticator
import com.example.messagingappmv.webservices.cavojsky.requestbodies.RefreshRequest
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RefreshResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.lang.IllegalStateException

object CavojskyWebService {

    private const val BASE_URL = "zadanie.mpage.sk"

    private fun create(context: Context) : CavojskyWebServiceInterface {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .authenticator(TokenAuthenticator(context))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .build()

        return retrofit.create(CavojskyWebServiceInterface::class.java)
    }

    private fun makeException(error: String) : IllegalStateException {
        return IllegalStateException("webservice call failed: $error")
    }

    fun refreshTokens(uid: String, refreshToken: String, context: Context) : RefreshResponse {
        val response = this.create(context).refreshTokens(RefreshRequest(uid, refreshToken)).execute()

        if(response.isSuccessful)
            return response.body()!!

        throw makeException(response.errorBody()?.string()!!)
    }
}