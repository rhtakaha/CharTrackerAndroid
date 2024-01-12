package com.chartracker.stories

import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chartracker.R
import com.chartracker.database.StoriesEntity
import com.chartracker.databinding.FragmentEditStoryBinding

class EditStoryFragment : Fragment(), MenuProvider{

    private lateinit var viewModel: EditStoryViewModel
    private lateinit var viewModelFactory: EditStoryViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditStoryBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner

        val args = EditStoryFragmentArgs.fromBundle(requireArguments())

        viewModelFactory = EditStoryViewModelFactory(args.storyId)
        viewModel = ViewModelProvider(this, viewModelFactory)[EditStoryViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.editStoryToStoriesNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(EditStoryFragmentDirections.actionEditStoryFragmentToStoriesFragment())
                viewModel.onEditStoryToStoriesNavigateComplete()
            }
        }

        viewModel.editStoryToCharactersNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EditStoryFragmentDirections.actionEditStoryFragmentToCharactersFragment(binding.editStoryTitle.text.toString()))
                viewModel.onEditStoryToCharactersNavigateComplete()
            }
        }



        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(EditStoryFragmentDirections.actionEditStoryFragmentToSettingsFragment())
                viewModel.onSettingsNavigateComplete()
            }
        }

        var imageURI = ""
        var imageType = ""

        binding.editStoriesSubmit.setOnClickListener {
            val story: StoriesEntity = if(imageURI != "" && imageType != ""){
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageType)
                StoriesEntity(binding.editStoryTitle.text.toString(),
                    binding.editStoryGenre.text.toString(),
                    binding.editStoryType.text.toString(),
                    binding.editStoryAuthor.text.toString(),
                    "story_${binding.editStoryTitle.text}.$extension")
            }else{
                // just updating the file and not the image could result in a differing filename and title
                // but as long as it points correctly it should be fine
                StoriesEntity(binding.editStoryTitle.text.toString(),
                    binding.editStoryGenre.text.toString(),
                    binding.editStoryType.text.toString(),
                    binding.editStoryAuthor.text.toString(),
                    viewModel.filename.value
                )
            }
            viewModel.submitStoryUpdate(story, imageURI)
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
                    .into(binding.editStorySelectedImage)

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.editStorySelectImageButton.setOnClickListener {
            chooseImage(pickMedia)
        }

        binding.editStoryRemoveSelectedImageButton.setOnClickListener {
            imageURI = ""
            imageType = ""
            binding.editStorySelectedImage.setImageResource(0)
        }

        binding.editStoryRemoveCurrentImageButton.setOnClickListener {
            viewModel.filename.value = null
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
        menuInflater.inflate(R.menu.action_bar_delete_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        // Handle the menu selection
        when(menuItem.itemId){
            android.R.id.home -> return false
            R.id.action_bar_settings -> viewModel.onSettingsNavigate()
            R.id.action_bar_delete -> viewModel.submitStoryDelete()
            else -> return true
        }
        return true
    }

}