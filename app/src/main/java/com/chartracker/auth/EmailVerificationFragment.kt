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

        viewModel.emailVerificationToStoriesNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EmailVerificationFragmentDirections.actionEmailVerificationFragmentToStoriesFragment())
                viewModel.onEmailVerificationToStoriesNavigateComplete()
            }
        }

        viewModel.emailVerificationToUpdateEmailNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EmailVerificationFragmentDirections.actionEmailVerificationFragmentToUpdateEmailFragment(false))
                viewModel.onEmailVerificationToUpdateEmailNavigateComplete()
            }
        }

        binding.emailVerifyCheck.setOnClickListener {
            if (viewModel.isEmailVerified()){
                //email is verified
                viewModel.onEmailVerificationToStoriesNavigate()
            }else{
                Toast.makeText(context, "Email not verified. It may take a moment", Toast.LENGTH_LONG).show()
            }
        }



        //send the verification email
        viewModel.sendVerificationEmail()

        return binding.root
    }

}