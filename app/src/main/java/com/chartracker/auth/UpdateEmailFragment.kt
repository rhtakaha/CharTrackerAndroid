package com.chartracker.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentUpdateEmailBinding

class UpdateEmailFragment : Fragment() {


    private lateinit var viewModel: UpdateEmailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUpdateEmailBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val args = UpdateEmailFragmentArgs.fromBundle(requireArguments())

            viewModel = ViewModelProvider(this)[UpdateEmailViewModel::class.java]
        binding.viewModel = viewModel

        // display the current email
        binding.updateEmailCurrentEmail.text = viewModel.user!!.email

        if (args.fromSettings){
            // if came from settings take them back there
            viewModel.updateEmailToSettingsNavigate.observe(viewLifecycleOwner) {
                if (it){
                    findNavController().navigate(UpdateEmailFragmentDirections.actionUpdateEmailFragmentToSettingsFragment())
                    viewModel.onUpdateEmailToSettingsNavigateComplete()
                }
            }
        }else{
            // if came from email verification then send back to sign in
            viewModel.updateEmailToSignInNavigate.observe(viewLifecycleOwner) {
                if (it){
                    findNavController().navigate(UpdateEmailFragmentDirections.actionUpdateEmailFragmentToSignInFragment())
                    viewModel.onUpdateEmailToSignInNavigateComplete()
                }
            }
        }

        binding.updateEmailSubmit.setOnClickListener {
            viewModel.updateUserEmail(binding.updateEmailNewInput.text.toString())
            if (args.fromSettings){
                viewModel.onUpdateEmailToSettingsNavigate()
            }else{
                viewModel.onUpdateEmailToSignInNavigate()
            }
        }

        return binding.root
    }



}