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

        viewModel = ViewModelProvider(this)[UpdateEmailViewModel::class.java]
        binding.viewModel = viewModel

        // display the current email
        binding.updateEmailCurrentEmail.text = viewModel.user!!.email

        viewModel.updateEmailToSettingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(UpdateEmailFragmentDirections.actionUpdateEmailFragmentToSettingsFragment())
                viewModel.onUpdateEmailToSettingsNavigateComplete()
            }
        }

        binding.updateEmailSubmit.setOnClickListener {
            viewModel.updateUserEmail(binding.updateEmailNewInput.text.toString())
            viewModel.onUpdateEmailToSettingsNavigate()
        }

        return binding.root
    }



}