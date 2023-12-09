package com.chartracker.characters


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chartracker.R
import com.chartracker.databinding.FragmentCharactersBinding

const val TAG = "CharFrag"
class CharactersFragment : Fragment() {

    private lateinit var viewModel: CharactersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentCharactersBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_characters, container, false)

        viewModel = CharactersViewModel()
        binding.charactersViewModel = viewModel

        val adapter = CharacterAdapter()
        binding.charactersList.adapter = adapter

        // observer for
        //let the adapter know when the stories changes
        viewModel.characters.observe(viewLifecycleOwner, Observer{
            it?.let {
                Log.i(TAG, "noticed change in the characters")
                adapter.submitList(it)
            }
        })

        viewModel.charactersToEditStoryNavigate.observe(viewLifecycleOwner, Observer {
            if (it){
                findNavController().navigate(CharactersFragmentDirections.actionCharactersFragmentToEditStoryFragment())
                viewModel.onCharactersToEditStoryNavigateComplete()
            }
        })

        //TODO will need to get this to the viewModel so we can run a db access method there to query off the title
        var args = CharactersFragmentArgs.fromBundle(requireArguments())
        Toast.makeText(context, "story navigated from ${args.storyTitle}", Toast.LENGTH_LONG).show()


        return binding.root
    }



}