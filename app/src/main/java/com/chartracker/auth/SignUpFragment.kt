package com.chartracker.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {


    private lateinit var viewModel: SignUpViewModel
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(inflater)

        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.viewModel = viewModel

        /* realistically probably don't need all this-----------------------------------------*/

        // Initialize Firebase Auth
        auth = Firebase.auth
        if (auth.currentUser != null){
            // if somehow signed in wouldn't want to be here
            viewModel.onSignUpNavigate()
            Toast.makeText(context, "already signed in", Toast.LENGTH_LONG).show()
        }
        /*---------------------------------------------------------------------*/

        viewModel.signUpNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
                viewModel.onSignUpNavigateComplete()
            }
        }

        viewModel.signUpFailed.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                viewModel.onSignUpFailedComplete()
            }
        }

        binding.signUpSubmit.setOnClickListener {
            if(binding.signUpPasswordFirst.text.toString() == binding.signUpPasswordSecond.text.toString()){
                // if the passwords match then go ahead
                viewModel.signUpUserWithEmailPassword(binding.signUpEmailAddress.text.toString(), binding.signUpPasswordFirst.toString())
            }else{
                //passwords don't match so let the user know
                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
            }

        }

        return binding.root
    }

}