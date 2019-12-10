package com.example.messagingappmv.screens.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.messagingappmv.R
import com.example.messagingappmv.databinding.FragmentSignupBinding

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        viewModel = ViewModelProviders.of(this).get(SignUpViewModel::class.java)
        binding.signupViewModel = viewModel
        binding.lifecycleOwner = this

        binding.buttonSignup.setOnClickListener {
            viewModel.register(this.context!!, this.findNavController())
        }

        return binding.root
    }
}