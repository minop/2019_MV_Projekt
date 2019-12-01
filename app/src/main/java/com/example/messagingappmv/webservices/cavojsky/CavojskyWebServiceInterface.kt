package com.example.messagingappmv.webservices.cavojsky

import com.example.messagingappmv.webservices.cavojsky.requestbodies.*
import com.example.messagingappmv.webservices.cavojsky.responsebodies.*
import retrofit.Call
import retrofit.http.POST

interface CavojskyWebServiceInterface {

    // user endpoints
    @POST("/user/create.php")
    fun register(body: RegistrationRequest) : Call<RegistrationResponse>

    @POST("/user/login.php")
    fun login(body: LoginRequest) : Call<LoginResponse>

    @POST("/user/refresh.php")
    fun refreshTokens(body: RefreshRequest) : Call<RefreshResponse>

    @POST("/user/fid.php")
    fun setFirebaseId(body: UserFirebaseRequest) : Call<Void>

    // room endpoints
    @POST("/room/list.php")
    fun getRooms(body: RoomListRequest) : Call<List<RoomListItem>>

    @POST("/room/message.php")
    fun sendMessageToRoom(body: RoomMessageRequest) : Call<Void>

    @POST("/room/read.php")
    fun getRoomMessages(body: RoomReadRequest) : Call<List<RoomReadItem>>

    // contact endpoint
    @POST("/contact/list.php")
    fun getContacts(body: ContactListRequest) : Call<List<ContactListItem>>

    @POST("/contact/message.php")
    fun sendMessageToContact(body: ContactMessageRequest) : Call<Void>

    @POST("/contact/read.php")
    fun getContactMessages(body: ContactReadRequest) : Call<List<ContactReadItem>>
}