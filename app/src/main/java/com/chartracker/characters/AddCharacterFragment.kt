package com.chartracker.characters

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chartracker.database.CharacterEntity
import com.chartracker.databinding.FragmentAddCharacterBinding

class AddCharacterFragment : Fragment() {

    private lateinit var viewModel: AddCharacterViewModel
    private lateinit var viewModelFactory: AddCharacterViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddCharacterBinding.inflate(inflater)

        val args = AddCharacterFragmentArgs.fromBundle(requireArguments())

        binding.lifecycleOwner = this

        viewModelFactory = AddCharacterViewModelFactory(args.storyId)
        viewModel = ViewModelProvider(this, viewModelFactory)[AddCharacterViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.addCharacterNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(AddCharacterFragmentDirections.actionAddCharacterFragmentToCharactersFragment(args.storyTitle))
                viewModel.onAddCharacterNavigateComplete()
            }
        }

        binding.addCharacterSubmit.setOnClickListener {
            viewModel.submitCharacter(
                CharacterEntity(
                    binding.addCharacterName.text.toString(),
                    binding.addCharacterAliases.text.toString(),
                    binding.addCharacterTitles.text.toString(),
                    if(binding.addCharacterAge.text.toString() != "") binding.addCharacterAge.text.toString().toInt() else null,
                    binding.addCharacterHome.text.toString(),
                    binding.addCharacterGender.text.toString(),
                    binding.addCharacterRace.text.toString(),
                    binding.addCharacterLivingOrDead.text.toString(),
                    binding.addCharacterOccupation.text.toString(),
                    binding.addCharacterWeapons.text.toString(),
                    binding.addCharacterToolsEquipment.text.toString(),
                    binding.addCharacterBio.text.toString(),
                    binding.addCharacterFaction.text.toString()
                    )
                )
        }

        return binding.root
    }


}