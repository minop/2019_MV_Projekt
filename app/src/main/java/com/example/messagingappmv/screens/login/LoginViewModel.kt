package com.example.messagingappmv.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage

class LoginViewModel(context: Context) : ViewModel() {
    var username = ""
    var password = ""

    init {
        if(TokenStorage.safeLoad(context) != null)
            println("TODO navigation")
    }

}