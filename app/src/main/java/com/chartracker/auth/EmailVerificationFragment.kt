package com.chartracker.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentEmailVerificationBinding

class EmailVerificationFragment : Fragment() {
    //TODO need to figure out how to hide the "up button"

    private lateinit var viewModel: EmailVerificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEmailVerificationBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner


        viewModel = ViewModelProvider(this)[EmailVerificationViewModel::class.java]
        binding.viewModel = viewModel

        // set the email to be shown to the user to make sure they used the correct one
        binding.emailVerifyEmail.text = viewModel.user?.email

        viewModel.emailVerificationToSignInNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EmailVerificationFragmentDirections.actionEmailVerificationFragmentToSignInFragment())
                viewModel.onEmailVerificationToSignInNavigateComplete()
            }
        }

        binding.emailVerifyCheck.setOnClickListener {
            viewModel.backToSignIn()
            Toast.makeText(context, "Sign in to continue", Toast.LENGTH_LONG).show()
        }

        //send the verification email
        viewModel.sendVerificationEmail()

        return binding.root
    }

}