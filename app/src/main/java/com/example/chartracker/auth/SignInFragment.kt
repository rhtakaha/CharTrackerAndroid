package com.example.chartracker.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chartracker.R
import com.example.chartracker.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {

//    companion object {
//        fun newInstance() = SignInFragment()
//    }

//    private val viewModel: SignInViewModel by lazy {
//        ViewModelProvider(this)[SignInViewModel::class.java]
//    }

    private lateinit var viewModel: SignInViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val binding: FragmentSignInBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_sign_in, container, false)

        val binding = FragmentSignInBinding.inflate(inflater)

        //allows LiveData to be monitored
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.signInNavigate.observe(viewLifecycleOwner, Observer {
                if (it){
                    findNavController().navigate(R.id.action_signInFragment_to_storiesFragment)
                    viewModel.onSignInNavigateComplete()
                }
        })

        return binding.root
    }

}