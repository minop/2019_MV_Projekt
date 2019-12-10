package com.example.messagingappmv.screens.signup

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.messagingappmv.R
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService

class SignUpViewModel : ViewModel() {
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val password2 = MutableLiveData<String>()

    init {
        username.value = ""
        password.value = ""
        password2.value = ""
    }

    fun register(context: Context, navController: NavController) {
        if(password.value.equals(password2.value)) {
            CavojskyWebService.register(username.value!!, password.value!!, context) {
                navController.navigate(R.id.action_signUpFragment_to_roomListFragment)
            }
            // TODO registration fails
        }
        else {
            // TODO error
        }
    }
}