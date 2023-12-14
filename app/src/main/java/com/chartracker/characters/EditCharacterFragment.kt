package com.chartracker.characters

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.chartracker.R
import com.chartracker.database.CharacterEntity
import com.chartracker.databinding.FragmentEditCharacterBinding
import com.google.android.material.chip.Chip

class EditCharacterFragment : Fragment() {

    private lateinit var viewModel: EditCharacterViewModel
    private lateinit var viewModelFactory: EditCharacterViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditCharacterBinding.inflate(inflater)

        binding.lifecycleOwner = this

        val args = EditCharacterFragmentArgs.fromBundle(requireArguments())

        viewModelFactory = EditCharacterViewModelFactory(args.storyId, args.charId, args.storyChars.toList())
        viewModel = ViewModelProvider(this, viewModelFactory)[EditCharacterViewModel::class.java]

        binding.viewModel = viewModel

        viewModel.character.observe(viewLifecycleOwner) {
            setupAllyChips(binding, viewModel, args.charName)
            setupEnemyChips(binding, viewModel, args.charName)
            setupNeutralChips(binding, viewModel, args.charName)
        }



        viewModel.editCharacterNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EditCharacterFragmentDirections.actionEditCharacterFragmentToCharacterDetailsFragment(args.charName, args.storyId, args.storyTitle, args.storyChars))
                viewModel.onEditCharacterNavigateComplete()
            }
        }

        binding.editCharacterSubmit.setOnClickListener {
            viewModel.submitCharacterUpdate(
                CharacterEntity(
                    binding.editCharacterName.text.toString(),
                    binding.editCharacterAliases.text.toString(),
                    binding.editCharacterTitles.text.toString(),
                    if(binding.editCharacterAge.text.toString() != "") binding.editCharacterAge.text.toString().toInt() else null,
                    binding.editCharacterHome.text.toString(),
                    binding.editCharacterGender.text.toString(),
                    binding.editCharacterRace.text.toString(),
                    binding.editCharacterLivingOrDead.text.toString(),
                    binding.editCharacterOccupation.text.toString(),
                    binding.editCharacterWeapons.text.toString(),
                    binding.editCharacterToolsEquipment.text.toString(),
                    binding.editCharacterBio.text.toString(),
                    binding.editCharacterFaction.text.toString(),
                    viewModel.allies,
                    viewModel.enemies,
                    viewModel.neutral
                )
            )
        }

        return binding.root
    }

    private fun setupAllyChips(binding: FragmentEditCharacterBinding, viewModel: EditCharacterViewModel, currentName: String){
        val alliesChipGroup = binding.editCharacterAlliesList
        val alliesInflater = LayoutInflater.from(alliesChipGroup.context)

        val alliesChildren: List<Chip> = viewModel.charsList.filter { it != currentName }.map { charName ->
            val chip =
                alliesInflater.inflate(R.layout.character_chip, alliesChipGroup, false) as Chip
            chip.text = charName
            chip.tag = charName
            chip.setOnCheckedChangeListener { button, isChecked ->
                viewModel.alliesUpdated(button.tag.toString(), isChecked)
            }
            chip

        }

        //clears any from before this latest refresh
        alliesChipGroup.removeAllViews()

        for (chip in alliesChildren){
            if(chip.tag in (viewModel.character.value?.allies ?: listOf())){
                //pre press the current allies
                chip.isChecked = true
                viewModel.alliesUpdated(chip.tag.toString(), true)
            }
            alliesChipGroup.addView(chip)
        }
    }

    private fun setupEnemyChips(binding: FragmentEditCharacterBinding, viewModel: EditCharacterViewModel, currentName: String){
        val enemiesChipGroup = binding.editCharacterEnemiesList
        val enemiesInflater = LayoutInflater.from(enemiesChipGroup.context)

        val enemiesChildren: List<Chip> = viewModel.charsList.filter { it != currentName }.map { charName ->
            val chip = enemiesInflater.inflate(R.layout.character_chip, enemiesChipGroup, false) as Chip
            chip.text = charName
            chip.tag = charName
            chip.setOnCheckedChangeListener { button, isChecked ->
                viewModel.enemiesUpdated(button.tag.toString(), isChecked)
            }
            chip
        }

        enemiesChipGroup.removeAllViews()

        for (chip in enemiesChildren){
            if(chip.tag in (viewModel.character.value?.enemies ?: listOf())){
                //pre press the current enemies
                chip.isChecked = true
                viewModel.enemiesUpdated(chip.tag.toString(), true)
            }
            enemiesChipGroup.addView(chip)
        }
    }

    private fun setupNeutralChips(binding: FragmentEditCharacterBinding, viewModel: EditCharacterViewModel, currentName: String){
        val neutralChipGroup = binding.editCharacterNeutralList
        val neutralInflater = LayoutInflater.from(neutralChipGroup.context)

        val neutralChildren: List<Chip> = viewModel.charsList.filter { it != currentName }.map { charName ->
            val chip = neutralInflater.inflate(R.layout.character_chip, neutralChipGroup, false) as Chip
            chip.text = charName
            chip.tag = charName
            chip.setOnCheckedChangeListener { button, isChecked ->
                viewModel.neutralsUpdated(button.tag.toString(), isChecked)
            }
            chip
        }

        neutralChipGroup.removeAllViews()

        for (chip in neutralChildren){
            if(chip.tag in (viewModel.character.value?.neutral ?: listOf())){
                //pre press the current neutrals
                chip.isChecked = true
                viewModel.neutralsUpdated(chip.tag.toString(), true)
            }
            neutralChipGroup.addView(chip)
        }
    }

}