package com.chartracker.stories


import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.chartracker.R
import com.chartracker.database.StoriesEntity
import com.chartracker.databinding.FragmentAddStoryBinding


class AddStoryFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: AddStoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAddStoryBinding.inflate(inflater)

        //allows LiveData to be monitored
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.addStoryNavigate.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToStoriesFragment())
                viewModel.onAddStoryNavigateComplete()
            }
        }

        viewModel.settingsNavigate.observe(viewLifecycleOwner) {
            if (it){
                findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToSettingsFragment())
                viewModel.onSettingsNavigateComplete()
            }
        }

        var imageURI = ""
        var imageType = ""


        binding.addStoriesSubmit.setOnClickListener {
            // build the story with or without the filename if based on if it has an image
            val story: StoriesEntity = if(imageURI != "" && imageType != ""){
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageType)
                StoriesEntity(binding.addStoryTitle.text.toString(),
                    binding.addStoryGenre.text.toString(),
                    binding.addStoryType.text.toString(),
                    binding.addStoryAuthor.text.toString(), "story_${binding.addStoryTitle.text}.$extension")
            }else{
                StoriesEntity(binding.addStoryTitle.text.toString(),
                    binding.addStoryGenre.text.toString(),
                    binding.addStoryType.text.toString(),
                    binding.addStoryAuthor.text.toString())
            }
            viewModel.submitStory(story, imageURI)
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
                    .into(binding.addStoryImage)

            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.addStoryChooseImageButton.setOnClickListener {
            chooseImage(pickMedia)
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


}