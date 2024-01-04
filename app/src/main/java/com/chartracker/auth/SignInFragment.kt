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

        viewModel.signInEmailUnverified.observe(viewLifecycleOwner) {
            if (it){
                Toast.makeText(context, "Email is unverified. Verify to continue.", Toast.LENGTH_LONG).show()
                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToEmailVerificationFragment())
                viewModel.onSignInEmailUnverifiedComplete()
            }
        }

        viewModel.sentPasswordReset.observe(viewLifecycleOwner){
            if (it) {
                Toast.makeText(context, "password reset email sent", Toast.LENGTH_LONG).show()
                viewModel.onSentPasswordResetComplete()
            }
        }

        binding.signInButton.setOnClickListener {
            if (binding.signInEmail.text.toString() != "" && binding.signInPassword.text.toString() != "") {
                viewModel.signInWithEmailPassword(binding.signInEmail.text.toString(), binding.signInPassword.text.toString())
            }else{
                Toast.makeText(context, "Enter email and password", Toast.LENGTH_LONG).show()
            }
        }

        binding.signInPasswordReset.setOnClickListener {
            if (binding.signInEmail.text.toString() != "") {
                viewModel.sendPasswordResetEmail(binding.signInEmail.text.toString())
            }else{
                Toast.makeText(context, "Enter email", Toast.LENGTH_LONG).show()
            }
        }

        binding.signInSavedCredentialsButton.setOnClickListener {
            viewModel.signInWithSavedCredentials()
        }

        if(viewModel.auth.currentUser != null){
            // if we are already signed in take us to the stories page
            viewModel.onSignInNavigate()
        }

        return binding.root
    }

}