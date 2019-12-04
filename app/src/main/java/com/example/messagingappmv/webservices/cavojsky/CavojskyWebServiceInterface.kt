package com.example.messagingappmv.webservices.cavojsky

import com.example.messagingappmv.webservices.cavojsky.requestbodies.*
import com.example.messagingappmv.webservices.cavojsky.responsebodies.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CavojskyWebServiceInterface {

    // user endpoints
    @POST("/user/create.php")
    fun register(@Body body: RegistrationRequest) : Call<RegistrationResponse>

    @POST("/user/login.php")
    fun login(@Body body: LoginRequest) : Call<LoginResponse>

    @POST("/user/refresh.php")
    fun refreshTokens(@Body body: RefreshRequest) : Call<RefreshResponse>

    @POST("/user/fid.php")
    fun setFirebaseId(@Body body: UserFirebaseRequest) : Call<Void>

    // room endpoints
    @POST("/room/list.php")
    fun getRooms(@Body body: RoomListRequest) : Call<List<RoomListItem>>

    @POST("/room/message.php")
    fun sendMessageToRoom(@Body body: RoomMessageRequest) : Call<Void>

    @POST("/room/read.php")
    fun getRoomMessages(@Body body: RoomReadRequest) : Call<List<RoomReadItem>>

    // contact endpoint
    @POST("/contact/list.php")
    fun getContacts(@Body body: ContactListRequest) : Call<List<ContactListItem>>

    @POST("/contact/message.php")
    fun sendMessageToContact(@Body body: ContactMessageRequest) : Call<Void>

    @POST("/contact/read.php")
    fun getContactMessages(@Body body: ContactReadRequest) : Call<List<ContactReadItem>>
}