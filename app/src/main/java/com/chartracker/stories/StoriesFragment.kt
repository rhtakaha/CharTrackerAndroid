package com.chartracker.stories

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
import com.chartracker.R
import com.chartracker.databinding.FragmentStoriesBinding


class StoriesFragment : Fragment(), MenuProvider {
    private val tag = "StoriesFrag"

    private lateinit var viewModel: StoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentStoriesBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[StoriesViewModel::class.java]
        binding.storiesViewModel = viewModel

        //adding recyclerview adapter
        val adapter = StoryAdapter(StoryListener {
            storyTitle ->  viewModel.onStoryClickedNavigate(storyTitle)
        })
        binding.storiesList.adapter = adapter

        viewModel.getStories()

        viewModel.storyClickedNavigate.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(
                    StoriesFragmentDirections.actionStoriesFragmentToCharactersFragment(it)
                )
                viewModel.onStoryClickedNavigateComplete()
            }
        }

        //let the adapter know when the stories changes
        viewModel.stories.observe(viewLifecycleOwner) {
            it?.let {
                Log.i(tag, "noticed change in the stories")
                adapter.submitList(it)
            }
        }

        //observer to navigate to adding a story
        viewModel.addStoryNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(StoriesFragmentDirections.actionStoriesFragmentToAddStoryFragment())
                viewModel.onAddStoryNavigateComplete()
            }
        }

        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(StoriesFragmentDirections.actionStoriesFragmentToSettingsFragment())
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