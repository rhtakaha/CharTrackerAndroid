package com.chartracker.stories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
                adapter.submitList(it)
            }
        })

        return binding.root
    }

}