package com.chartracker.util

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingsBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        binding.viewmodel = viewModel

        viewModel.signOutNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSignInFragment())
                viewModel.onSignOutNavigateComplete()
            }
        }

        viewModel.settingsToUpdateEmailNavigate.observe(viewLifecycleOwner){
            if (it){
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUpdateEmailFragment(true))
                viewModel.onSettingsToUpdateEmailNavigateComplete()
            }
        }

        viewModel.settingsToUpdatePasswordNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUpdatePasswordFragment())
                viewModel.onSettingsToUpdatePasswordNavigateComplete()
            }
        }

        viewModel.settingsToSignInNavigate.observe(viewLifecycleOwner){
            if (it){
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSignInFragment())
                viewModel.onSettingsToSignInNavigateComplete()
            }
        }

        binding.settingsReauthButton.setOnClickListener {
            viewModel.reauthUser(binding.settingsPassword.text.toString())
        }

        return binding.root
    }

}