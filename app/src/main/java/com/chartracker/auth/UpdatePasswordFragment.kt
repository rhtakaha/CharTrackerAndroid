package com.chartracker.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentUpdatePasswordBinding

class UpdatePasswordFragment : Fragment() {


    private lateinit var viewModel: UpdatePasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUpdatePasswordBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[UpdatePasswordViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.updatePasswordToSettingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(UpdatePasswordFragmentDirections.actionUpdatePasswordFragmentToSettingsFragment())
                viewModel.onUpdatePasswordToSettingsNavigateComplete()
            }
        }

        binding.updatePasswordSubmit.setOnClickListener {
            if(binding.updatePasswordInput.text.toString() == binding.updatePasswordConfirmInput.text.toString()){
                // if the passwords match then go ahead
                viewModel.updatePassword(binding.updatePasswordInput.text.toString())
            }else{
                //passwords don't match so let the user know
                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_LONG).show()
            }
        }


        return binding.root
    }


}