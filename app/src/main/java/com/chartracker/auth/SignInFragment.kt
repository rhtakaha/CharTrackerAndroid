package com.chartracker.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

    private lateinit var viewModel: SignInViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentSignInBinding.inflate(inflater)

        //allows LiveData to be monitored
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[SignInViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.signInNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToStoriesFragment())
                viewModel.onSignInNavigateComplete()
            }
        }

        viewModel.signInToSignUpNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
                viewModel.onSignInToSignUpNavigateComplete()
            }
        }

        viewModel.signInFailed.observe(viewLifecycleOwner) {
            if (it){
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                viewModel.onSignInFailedComplete()
            }
        }

        binding.signInButton.setOnClickListener {
            if (binding.signInEmail.text.toString() != "" && binding.signInPassword.text.toString() != "") {
                viewModel.signInWithEmailPassword(binding.signInEmail.text.toString(), binding.signInPassword.text.toString())
            }else{
                Toast.makeText(context, "Enter email and password", Toast.LENGTH_LONG).show()
            }
        }

        binding.signInSavedCredentialsButton.setOnClickListener {
            viewModel.signInWithSavedCredentials()
        }

        return binding.root
    }

}