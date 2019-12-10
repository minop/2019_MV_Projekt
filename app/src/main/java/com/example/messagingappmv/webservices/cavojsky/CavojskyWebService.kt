package com.example.messagingappmv.webservices.cavojsky

import android.content.Context
import com.example.messagingappmv.webservices.cavojsky.interceptors.AuthInterceptor
import com.example.messagingappmv.webservices.cavojsky.interceptors.LoginData
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenAuthenticator
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage
import com.example.messagingappmv.webservices.cavojsky.requestbodies.*
import com.example.messagingappmv.webservices.cavojsky.responsebodies.*
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
    fun register(username: String, password: String, context: Context, callback: () -> Unit = {}, callbackError: () -> Unit = {}) {
        WebserviceTask<RegistrationResponse>({ response ->
            TokenStorage.save(LoginData(response!!.uid, response.access, response.refresh), context)
            callback.invoke()
        }, callbackError).execute(
            this.create(context).register(RegistrationRequest(username, password))
        )
    }

    fun login(username: String, password: String, context: Context, callback: () -> Unit = {}, callbackError: () -> Unit = {}) {
        WebserviceTask<LoginResponse>({ response ->
            TokenStorage.save(LoginData(response!!.uid, response.access, response.refresh), context)
            callback.invoke()
        }, callbackError).execute(
            this.create(context).login(LoginRequest(username, password))
        )
    }

    fun refreshTokens(uid: String, refreshToken: String, context: Context) : RefreshResponse {
        val response = this.create(context).refreshTokens(RefreshRequest(uid, refreshToken)).execute()

        if(response.isSuccessful)
            return response.body()!!

        throw makeException(response.errorBody()?.string()!!)
    }

    // TODO firebase

    fun listRooms(context: Context, callback: (List<RoomListItem>) -> Unit, callbackError: () -> Unit = {}) {
        WebserviceTask<List<RoomListItem>>({ response ->
            callback.invoke(response!!)
        }, callbackError).execute(
            this.create(context).getRooms(RoomListRequest( TokenStorage.load(context).uid ))
        )
    }

    fun sendMessageToRoom(room: String, message: String, context: Context, callback: () -> Unit = {}, callbackError: () -> Unit = {}) {
        WebserviceTask<Unit>({
            callback.invoke()
        }, callbackError).execute(
            this.create(context).sendMessageToRoom(RoomMessageRequest(TokenStorage.load(context).uid, room, message))
        )
    }

    fun getRoomMessages(room: String, context: Context, callback: (List<RoomReadItem>) -> Unit, callbackError: () -> Unit = {}) {
        WebserviceTask<List<RoomReadItem>>({ response ->
            callback.invoke(response!!)
        }, callbackError).execute(
            this.create(context).getRoomMessages(RoomReadRequest(TokenStorage.load(context).uid, room))
        )
    }

    fun listContacts(context: Context, callback: (List<ContactListItem>) -> Unit, callbackError: () -> Unit = {}) {
        WebserviceTask<List<ContactListItem>>({ response ->
            callback.invoke(response!!)
        }, callbackError).execute(
            this.create(context).getContacts(ContactListRequest( TokenStorage.load(context).uid ))
        )
    }

    fun sendMessageToContact(contact: String, message: String, context: Context, callback: () -> Unit = {}, callbackError: () -> Unit = {}) {
        WebserviceTask<Unit>({
            callback.invoke()
        }, callbackError).execute(
            this.create(context).sendMessageToContact(ContactMessageRequest(TokenStorage.load(context).uid, contact, message))
        )
    }

    fun getContactMessages(contact: String, context: Context, callback: (List<ContactReadItem>) -> Unit, callbackError: () -> Unit = {}) {
        WebserviceTask<List<ContactReadItem>>({ response ->
            callback.invoke(response!!)
        }, callbackError).execute(
            this.create(context).getContactMessages(ContactReadRequest(TokenStorage.load(context).uid, contact))
        )
    }
}