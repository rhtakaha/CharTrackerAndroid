package com.chartracker.stories

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chartracker.R
import com.chartracker.database.StoriesEntity
import com.chartracker.databinding.FragmentEditStoryBinding

class EditStoryFragment : Fragment(), MenuProvider{

    private lateinit var viewModel: EditStoryViewModel
    private lateinit var viewModelFactory: EditStoryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditStoryBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val args = EditStoryFragmentArgs.fromBundle(requireArguments())

        viewModelFactory = EditStoryViewModelFactory(args.storyId)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditStoryViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.editStoryNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(EditStoryFragmentDirections.actionEditStoryFragmentToStoriesFragment())
                viewModel.onEditStoryNavigateComplete()
            }
        }

        binding.editStoriesSubmit.setOnClickListener {
            viewModel.submitStoryUpdate(
                StoriesEntity(binding.editStoryTitle.text.toString(),
                    binding.editStoryGenre.text.toString(),
                    binding.editStoryType.text.toString(),
                    binding.editStoryAuthor.text.toString())
            )
        }

        binding.editStoriesDelete.setOnClickListener {
            viewModel.submitStoryDelete()
        }

        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EditStoryFragmentDirections.actionEditStoryFragmentToSettingsFragment())
                viewModel.onSettingsNavigateComplete()
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Add menu items here
        menuInflater.inflate(R.menu.action_bar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Handle the menu selection
        when(menuItem.itemId){
            android.R.id.home -> return false
            R.id.action_bar_settings -> viewModel.onSettingsNavigate()
            else -> return true
        }
        return true
    }

}