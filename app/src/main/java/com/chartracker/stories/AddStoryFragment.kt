package com.chartracker.stories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chartracker.database.StoriesEntity
import com.chartracker.databinding.FragmentAddStoryBinding

class AddStoryFragment : Fragment() {

    private lateinit var viewModel: AddStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddStoryBinding.inflate(inflater)

        //allows LiveData to be monitored
        binding.lifecycleOwner = this

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

        return binding.root
    }


}