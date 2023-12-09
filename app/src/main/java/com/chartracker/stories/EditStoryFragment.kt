package com.chartracker.stories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chartracker.databinding.FragmentEditStoryBinding

class EditStoryFragment : Fragment() {

    private lateinit var viewModel: EditStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditStoryBinding.inflate(inflater)

        binding.lifecycleOwner = this

        viewModel = EditStoryViewModel()
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