package com.example.messagingappmv.screens.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messagingappmv.R
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService

class LoginViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val error = MutableLiveData<String>()

    init {
        username.value = ""
        password.value = ""
        error.value = ""
    }

    fun login(context: Context, navController: NavController) {
        CavojskyWebService.login(username.value!!, password.value!!, context) {
            navController.navigate(R.id.action_loginFragment_to_roomListFragment)
        }
        // TODO login fails
    }
}