package com.example.messagingappmv.webservices.cavojsky.requestbodies

class RoomMessageRequest(val uid: String, val room : String, val message : String) : BaseRequest()