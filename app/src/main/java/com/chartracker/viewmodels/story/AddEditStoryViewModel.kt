package com.chartracker.viewmodels.story

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditStoryViewModel(private val storyId: String?): ViewModel() {
    private val tag = "AddEditStoryVM"
    private val db = DatabaseAccess()
    private var originalFilename: String? = null
    init {
        if (storyId != null) {
            getStory(storyId= storyId)
        }
    }

    private val _story = mutableStateOf(StoryEntity())
    val story: MutableState<StoryEntity>
        get() = _story

    private fun updateStory(newStory: StoryEntity){
        _story.value = newStory
    }

    /*navigate back to stories event*/
    private val _navToStories = mutableStateOf(false)
    val navToStories: MutableState<Boolean>
        get() = _navToStories

    fun resetNavToStories(){
        _navToStories.value = false
    }

    /*function that calls a database access method to create the story in Firebase
        also calls navigation*/
    fun submitStory(newStory: StoryEntity, localImageURI: Uri?){
        viewModelScope.launch {
            if (storyId == null) {
                addStory(newStory, localImageURI)
            }else{
                updateStory(storyId, newStory, localImageURI)
            }
            _navToStories.value = true
        }
    }

    private suspend fun updateStory(storyId: String, updatedStory: StoryEntity, localImageURI: Uri?){
        Log.i(tag, "starting to update story")


        if (localImageURI != null){
            if (!localImageURI.toString().startsWith("https://firebasestorage.googleapis.com")){
                // if this was NOT the original image
                // trying to add a new image
                // no matter what adding the new image
                updatedStory.imageFilename.value = getStoryFilename(updatedStory.name.value)
                Log.i(tag, "adding new image to story with new filename: ${updatedStory.imageFilename.value}")
                db.addImage(updatedStory.imageFilename.value!!, localImageURI)
                db.addImageDownloadUrlToStory(updatedStory, updatedStory.imageFilename.value!!)

                //if adding a new image be sure to delete the original too (if it had one)
                originalFilename?.let {
                        it1 ->
                    Log.i(tag, "deleting original story image with original filename: $it1")
                    db.deleteImage(it1)
                }
            }
            // if this was just the existing image don't do anything

        }else{
            // could be either making no image change or trying to delete it
            if (originalFilename != null) {
                // if there is no file in new version (due to localImageURI == null)
                //                  AND
                // the old version had one then we are deleting the current
                Log.i(tag, "deleting original story image with original filename: $originalFilename")
                db.deleteImage(originalFilename!!)
                updatedStory.imageFilename.value = null
                updatedStory.imagePublicUrl.value = null
            }
            // if both were null it would be that there started with and ended with no image
        }
        db.updateStory(storyId, updatedStory)
    }

    private suspend fun addStory(newStory: StoryEntity, localImageURI: Uri?){
        if (localImageURI != null) {
            // if we are adding an image
            newStory.imageFilename.value = getStoryFilename(newStory.name.value)
            db.addImage(newStory.imageFilename.value!!, localImageURI)
            db.addImageDownloadUrlToStory(newStory, newStory.imageFilename.value!!)
        }
        Log.i(tag, "Creation of new story initiated")
        db.createStory(newStory)
    }

    /* function which gets the story given the id*/
    private fun getStory(storyId: String){
        viewModelScope.launch {
            updateStory(db.getStoryFromId(storyId))
            originalFilename = story.value.imageFilename.value
        }
    }
}

/* time ensures that filenames are unique:
            can't go back in time even if you add story w/ image,
                change that story's name, (image keeps old name),
                and then try to add a story with the original story name
                    (which would have created two images with the same name)*/
fun getStoryFilename(title: String) = "story_${title}_${Calendar.getInstance().time}"

class AddEditStoryViewModelFactory(private val storyId: String?) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditStoryViewModel(storyId) as T
}