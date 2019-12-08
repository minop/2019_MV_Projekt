package com.example.messagingappmv.webservices.cavojsky

import com.example.messagingappmv.webservices.cavojsky.requestbodies.*
import com.example.messagingappmv.webservices.cavojsky.responsebodies.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
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
    @Headers("ZadanieApiAuth: accept")
    fun setFirebaseId(@Body body: UserFirebaseRequest) : Call<Unit>

    // room endpoints
    @POST("/room/list.php")
    @Headers("ZadanieApiAuth: accept")
    fun getRooms(@Body body: RoomListRequest) : Call<List<RoomListItem>>

    @POST("/room/message.php")
    @Headers("ZadanieApiAuth: accept")
    fun sendMessageToRoom(@Body body: RoomMessageRequest) : Call<Unit>

    @POST("/room/read.php")
    @Headers("ZadanieApiAuth: accept")
    fun getRoomMessages(@Body body: RoomReadRequest) : Call<List<RoomReadItem>>

    // contact endpoint
    @POST("/contact/list.php")
    @Headers("ZadanieApiAuth: accept")
    fun getContacts(@Body body: ContactListRequest) : Call<List<ContactListItem>>

    @POST("/contact/message.php")
    @Headers("ZadanieApiAuth: accept")
    fun sendMessageToContact(@Body body: ContactMessageRequest) : Call<Unit>

    @POST("/contact/read.php")
    @Headers("ZadanieApiAuth: accept")
    fun getContactMessages(@Body body: ContactReadRequest) : Call<List<ContactReadItem>>
}