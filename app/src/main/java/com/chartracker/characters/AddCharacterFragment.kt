package com.chartracker.characters

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chartracker.R
import com.chartracker.database.CharacterEntity
import com.chartracker.databinding.FragmentAddCharacterBinding
import com.google.android.material.chip.Chip

class AddCharacterFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: AddCharacterViewModel
    private lateinit var viewModelFactory: AddCharacterViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddCharacterBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val args = AddCharacterFragmentArgs.fromBundle(requireArguments())

        viewModelFactory = AddCharacterViewModelFactory(args.storyId, args.storyChars.toList())
        viewModel = ViewModelProvider(this, viewModelFactory)[AddCharacterViewModel::class.java]
        binding.viewModel = viewModel

        setupAllyChips(binding, viewModel)
        setupEnemyChips(binding, viewModel)
        setupNeutralChips(binding, viewModel)

        viewModel.addCharacterNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(AddCharacterFragmentDirections.actionAddCharacterFragmentToCharactersFragment(args.storyTitle))
                viewModel.onAddCharacterNavigateComplete()
            }
        }



        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(AddCharacterFragmentDirections.actionAddCharacterFragmentToSettingsFragment())
                viewModel.onSettingsNavigateComplete()
            }
        }

        var imageURI = ""
        var imageType = ""

        binding.addCharacterSubmit.setOnClickListener {
            val character: CharacterEntity = if(imageURI != "" && imageType != ""){
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageType)
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
                    binding.addCharacterFaction.text.toString(),
                    viewModel.allies,
                    viewModel.enemies,
                    viewModel.neutral,
                    "character_${args.storyTitle}_${binding.addCharacterName.text}.$extension"
                )
            }else{
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
                    binding.addCharacterFaction.text.toString(),
                    viewModel.allies,
                    viewModel.enemies,
                    viewModel.neutral
                )
            }
            viewModel.submitCharacter(character, imageURI)
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                val type = requireContext().contentResolver.getType(uri)
                Log.d("PhotoPicker", "Selected URI: $uri of type: $type")
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)

                if (type != null) {
                    imageType = type
                }
                imageURI = uri.toString()

                Glide.with(requireContext())
                    .load(uri)
                    .into(binding.addCharacterImage)

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.addCharacterSelectImageButton.setOnClickListener {
            chooseImage(pickMedia)
        }

        binding.addCharacterRemoveImageButton.setOnClickListener {
            imageURI = ""
            imageType = ""
            binding.addCharacterImage.setImageResource(0)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    private fun chooseImage(pickMedia: ActivityResultLauncher<PickVisualMediaRequest>){
        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        // Add menu items here
        menuInflater.inflate(R.menu.action_bar_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Handle the menu selection
        when(menuItem.itemId){
            android.R.id.home -> return false
            R.id.action_bar_settings -> viewModel.onSettingsNavigate()
            else -> return true
        }
        return true
    }

    private fun setupAllyChips(binding: FragmentAddCharacterBinding, viewModel: AddCharacterViewModel){
        val alliesChipGroup = binding.addCharacterAlliesList
        val alliesInflater = LayoutInflater.from(alliesChipGroup.context)

        val alliesChildren: List<Chip> = viewModel.charsList.map { charName ->
            val chip = alliesInflater.inflate(R.layout.character_chip, alliesChipGroup, false) as Chip
            chip.text = charName
            chip.tag = charName
            chip.setOnCheckedChangeListener { button, isChecked ->
                viewModel.alliesUpdated(button.tag.toString(), isChecked)
                Log.i("AddCharFrag", "changing ${button.tag}")
            }
            chip
        }

        for (chip in alliesChildren){
            alliesChipGroup.addView(chip)
        }
    }

    private fun setupEnemyChips(binding: FragmentAddCharacterBinding, viewModel: AddCharacterViewModel){
        val enemiesChipGroup = binding.addCharacterEnemiesList
        val enemiesInflater = LayoutInflater.from(enemiesChipGroup.context)

        val enemiesChildren: List<Chip> = viewModel.charsList.map { charName ->
            val chip = enemiesInflater.inflate(R.layout.character_chip, enemiesChipGroup, false) as Chip
            chip.text = charName
            chip.tag = charName
            chip.setOnCheckedChangeListener { button, isChecked ->
                viewModel.enemiesUpdated(button.tag.toString(), isChecked)
                Log.i("AddCharFrag", "changing ${button.tag}")
            }
            chip
        }

        for (chip in enemiesChildren){
            enemiesChipGroup.addView(chip)
        }
    }

    private fun setupNeutralChips(binding: FragmentAddCharacterBinding, viewModel: AddCharacterViewModel){
        val neutralChipGroup = binding.addCharacterNeutralList
        val neutralInflater = LayoutInflater.from(neutralChipGroup.context)

        val neutralChildren: List<Chip> = viewModel.charsList.map { charName ->
            val chip = neutralInflater.inflate(R.layout.character_chip, neutralChipGroup, false) as Chip
            chip.text = charName
            chip.tag = charName
            chip.setOnCheckedChangeListener { button, isChecked ->
                viewModel.neutralsUpdated(button.tag.toString(), isChecked)
                Log.i("AddCharFrag", "changing ${button.tag}")
            }
            chip
        }

        for (chip in neutralChildren){
            neutralChipGroup.addView(chip)
        }
    }
}