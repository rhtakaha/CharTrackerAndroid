package com.chartracker.characters

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentCharacterDetailsBinding

class CharacterDetailsFragment : Fragment() {

    private lateinit var viewModel: CharacterDetailsViewModel
    private lateinit var viewModelFactory: CharacterDetailsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCharacterDetailsBinding.inflate(inflater)

        val args = CharacterDetailsFragmentArgs.fromBundle(requireArguments())

        binding.lifecycleOwner = this

        viewModelFactory = CharacterDetailsViewModelFactory(args.storyId, args.storyTitle, args.charName)
        viewModel = ViewModelProvider(this, viewModelFactory)[CharacterDetailsViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.charDetailsToCharsNavigate.observe(viewLifecycleOwner) {
            if(it) {
                findNavController().navigate(
                    CharacterDetailsFragmentDirections.actionCharacterDetailsFragmentToCharactersFragment(args.storyTitle)
                )
                viewModel.onCharDetailsToCharsNavigateComplete()
            }
        }

        viewModel.charDetailsToEditCharNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(
                    CharacterDetailsFragmentDirections.actionCharacterDetailsFragmentToEditCharacterFragment(args.charName, args.storyId, args.storyTitle, viewModel.charId, args.storyChars)
                )
                viewModel.onCharDetailsToEditCharNavigateComplete()
            }
        }

        return binding.root
    }

}