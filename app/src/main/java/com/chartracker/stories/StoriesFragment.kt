package com.chartracker.stories

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chartracker.R
import com.chartracker.databinding.FragmentStoriesBinding

class StoriesFragment : Fragment() {

    private lateinit var viewModel: StoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentStoriesBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_stories, container, false)

        //adding recyclerview adapter
        val adapter = StoryAdapter()
        binding.storiesList.adapter = adapter

        viewModel = StoriesViewModel()
        binding.storiesViewModel = viewModel

        //let the adapter know when the stories changes
        viewModel.stories.observe(viewLifecycleOwner, Observer{
            it?.let {
                Log.i("StoriesFrag", "noticed change in the stories")
                adapter.submitList(it)
            }
        })

        //observer to navigate to adding a story
        viewModel.addStoryNavigate.observe(viewLifecycleOwner, Observer {
            if (it){
                findNavController().navigate(R.id.action_storiesFragment_to_addStoryFragment)
                viewModel.onAddStoryNavigateComplete()
            }
        })

        return binding.root
    }

}