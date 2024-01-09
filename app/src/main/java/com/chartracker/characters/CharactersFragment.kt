package com.chartracker.characters


import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chartracker.R
import com.chartracker.databinding.FragmentCharactersBinding

class CharactersFragment : Fragment(), MenuProvider {
    private val tag = "CharFrag"

    private lateinit var viewModel: CharactersViewModel
    private lateinit var viewModelFactory: CharactersViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCharactersBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val args = CharactersFragmentArgs.fromBundle(requireArguments())

        viewModelFactory = CharactersViewModelFactory(args.storyTitle)
        viewModel = ViewModelProvider(this, viewModelFactory)[CharactersViewModel::class.java]
        binding.charactersViewModel = viewModel

        val adapter = CharacterAdapter(CharacterListener {
            charName ->  viewModel.onCharacterClickedNavigate(charName)
        })
        binding.charactersList.adapter = adapter

        viewModel.characterClickedNavigate.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    CharactersFragmentDirections.actionCharactersFragmentToCharacterDetailsFragment(it, viewModel.storyId, args.storyTitle, viewModel.charactersStringList.toTypedArray())
                )
                viewModel.onCharacterClickedNavigateComplete()
            }
        }

        // observer for
        //let the adapter know when the stories changes
        viewModel.characters.observe(viewLifecycleOwner) {
            it?.let {
                Log.i(tag, "noticed change in the characters")
                adapter.submitList(it)
                viewModel.updateCharsStringList()
            }
        }

        viewModel.charactersToEditStoryNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(
                    CharactersFragmentDirections.actionCharactersFragmentToEditStoryFragment(viewModel.storyId)
                )
                viewModel.onCharactersToEditStoryNavigateComplete()
            }
        }

        viewModel.charactersToAddCharacterNavigate.observe(viewLifecycleOwner){
            if (it){
                findNavController().navigate(
                    CharactersFragmentDirections.actionCharactersFragmentToAddCharacterFragment(viewModel.storyId, args.storyTitle, viewModel.charactersStringList.toTypedArray())
                )
                viewModel.onCharactersToAddCharacterNavigateComplete()
            }
        }

        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(CharactersFragmentDirections.actionCharactersFragmentToSettingsFragment())
                viewModel.onSettingsNavigateComplete()
            }
        }

        viewModel.story.observe(viewLifecycleOwner){
            it?.let {
                //get and display
                // Reference to an image file in Cloud Storage
                val imageRef = viewModel.getImageRef()

                // Download directly from StorageReference using Glide
                Glide.with(requireContext())
                    .load(imageRef)
                    .into(binding.charactersStoryImage)
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
            R.id.action_bar_edit -> viewModel.onCharactersToEditStoryNavigate()
            else -> return true
        }
        return true
    }
}