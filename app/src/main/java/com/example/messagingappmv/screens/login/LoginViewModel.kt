package com.example.messagingappmv.screens.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messagingappmv.R
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage

class LoginViewModel(context: Context, navController: NavController) : ViewModel() {
    var username = ""
    var password = ""

    init {
        if(TokenStorage.safeLoad(context) != null)
            navController.navigate(R.id.action_loginFragment_to_roomListFragment)
    }

    fun login(context: Context, navController: NavController) {
        CavojskyWebService.login(username, password, context) {
            navController.navigate(R.id.action_loginFragment_to_roomListFragment)
        }
        // TODO login fails
    }
}