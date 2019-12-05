package com.example.messagingappmv.webservices.cavojsky.requestbodies

class ContactMessageRequest(val uid: String, val contact : String, val message : String) : BaseRequest()