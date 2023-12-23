package com.chartracker.characters

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.chartracker.R
import com.chartracker.databinding.FragmentCharacterDetailsBinding

class CharacterDetailsFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: CharacterDetailsViewModel
    private lateinit var viewModelFactory: CharacterDetailsViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCharacterDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val args = CharacterDetailsFragmentArgs.fromBundle(requireArguments())

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

        binding.characterDetailsDelete.setOnClickListener {
            viewModel.submitCharacterDelete()
        }

        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(CharacterDetailsFragmentDirections.actionCharacterDetailsFragmentToSettingsFragment())
                viewModel.onSettingsNavigateComplete()
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Add menu items here
        menuInflater.inflate(R.menu.action_bar_edit_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Handle the menu selection
        when(menuItem.itemId){
            android.R.id.home -> return false
            R.id.action_bar_settings -> viewModel.onSettingsNavigate()
            R.id.action_bar_edit -> viewModel.onCharDetailsToEditCharNavigate()
            else -> return true
        }
        return true
    }

}