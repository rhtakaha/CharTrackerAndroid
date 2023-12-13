package com.chartracker.stories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chartracker.R
import com.chartracker.databinding.FragmentStoriesBinding


class StoriesFragment : Fragment() {
    private val tag = "StoriesFrag"

    private lateinit var viewModel: StoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStoriesBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_stories, container, false)

        viewModel = ViewModelProvider(this)[StoriesViewModel::class.java]
        binding.storiesViewModel = viewModel

        //adding recyclerview adapter
        val adapter = StoryAdapter(StoryListener {
            storyTitle ->  viewModel.onStoryClickedNavigate(storyTitle)
        })
        binding.storiesList.adapter = adapter

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

        return binding.root
    }

}