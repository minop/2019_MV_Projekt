package com.example.messagingappmv.screens.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messagingappmv.R
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import com.example.messagingappmv.webservices.firebase.FirebaseWebService

class LoginViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val logedIn = MutableLiveData<Boolean>()

    val error = MutableLiveData<String>()

    init {
        username.value = ""
        password.value = ""
        error.value = ""
        logedIn.value = true
    }

    fun login(context: Context, navController: NavController) {
        error.value = ""

        CavojskyWebService.login(username.value!!, password.value!!, context, {
            FirebaseWebService.forceTokenRegistration(context)
            navController.navigate(R.id.action_loginFragment_to_roomListFragment)
            logedIn.value = false
        }, {
            error.value = "login failed"
            logedIn.value = true
        })
    }
}