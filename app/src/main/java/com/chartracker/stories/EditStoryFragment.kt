package com.chartracker.stories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentEditStoryBinding

class EditStoryFragment : Fragment() {

    private lateinit var viewModel: EditStoryViewModel
    private lateinit var viewModelFactory: EditStoryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditStoryBinding.inflate(inflater)

        binding.lifecycleOwner = this

        val args = EditStoryFragmentArgs.fromBundle(requireArguments())

        viewModelFactory = EditStoryViewModelFactory(args.storyTitle)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditStoryViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.editStoryNavigate.observe(viewLifecycleOwner, Observer {
            if (it){
                findNavController().navigate(EditStoryFragmentDirections.actionEditStoryFragmentToStoriesFragment())
                viewModel.onEditStoryNavigateComplete()
            }
        })

        return binding.root
    }

}