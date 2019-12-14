package com.example.messagingappmv.screens.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.messagingappmv.DrawerMenuLocker
import com.example.messagingappmv.R
import com.example.messagingappmv.databinding.FragmentLoginBinding
import com.example.messagingappmv.webservices.cavojsky.interceptors.TokenStorage

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        binding.loginViewModel = viewModel
        binding.lifecycleOwner = this

        // dirty but effective
        if (TokenStorage.containsToken(this.context!!))
            TokenStorage.delete(this.context!!)

        binding.buttonLogin.setOnClickListener {
            viewModel.login(this.context!!, this.findNavController())
        }

        binding.buttonSignup.setOnClickListener {
            this.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        viewModel.logedIn.observe(this, Observer {
            (activity as DrawerMenuLocker).setDrawerLocked(it)
            
            Log.d("LogedIn", "User successfully loged")
        })

        return binding.root
    }
}