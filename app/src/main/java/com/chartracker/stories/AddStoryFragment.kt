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
import com.chartracker.databinding.FragmentAddStoryBinding

class AddStoryFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: AddStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddStoryBinding.inflate(inflater)

        //allows LiveData to be monitored
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.addStoryNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToStoriesFragment())
                viewModel.onAddStoryNavigateComplete()
            }
        }

        binding.addStoriesSubmit.setOnClickListener {
            viewModel.submitStory(StoriesEntity(binding.addStoryTitle.text.toString(),
                binding.addStoryGenre.text.toString(),
                binding.addStoryType.text.toString(),
                binding.addStoryAuthor.text.toString()))
        }

        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToSettingsFragment())
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