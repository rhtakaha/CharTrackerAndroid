package com.example.chartracker.stories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.chartracker.R
import com.example.chartracker.databinding.FragmentStoriesBinding

class StoriesFragment : Fragment() {

    companion object {
        fun newInstance() = StoriesFragment()
    }

    private lateinit var viewModel: StoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStoriesBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_stories, container, false)
        return binding.root
    }

}