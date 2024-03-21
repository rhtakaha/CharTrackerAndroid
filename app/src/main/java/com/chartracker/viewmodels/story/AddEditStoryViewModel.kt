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
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddEditStoryViewModel(private val storyId: String?): ViewModel() {
    private val tag = "AddEditStoryVM"
    private val db = DatabaseAccess()
    private var originalFilename: String? = null
    private lateinit var originalStoryTitle: String
    private var currentTitles: MutableList<String>? = null

    private val _story = mutableStateOf(StoryEntity())
    val story: MutableState<StoryEntity>
        get() = _story

    /*navigate back to stories event*/
    private val _navToStories = mutableStateOf(false)
    val navToStories: MutableState<Boolean>
        get() = _navToStories

    fun resetNavToStories(){
        _navToStories.value = false
    }

    /*upload error event*/
    private val _uploadError = mutableStateOf(false)
    val uploadError: MutableState<Boolean>
        get() = _uploadError

    fun resetUploadError(){
        _uploadError.value = false
    }

    /*duplicate title error event*/
    private val _duplicateTitleError = mutableStateOf(false)
    val duplicateTitleError: MutableState<Boolean>
        get() = _duplicateTitleError

    fun resetDuplicateTitleError(){
        _duplicateTitleError.value = false
    }

    init {
        if (storyId != null) {
            getStory(storyId= storyId)
        }
        viewModelScope.launch {
            currentTitles = db.getCurrentTitles()
            if (currentTitles == null){
                // if we could not get the current titles
                _uploadError.value = true
            }
        }
    }

    /*function that calls a database access method to create the story in Firebase
        also calls navigation*/
    fun submitStory(newStory: StoryEntity, localImageURI: Uri?){

        viewModelScope.launch {
            newStory.accessDate.value = Timestamp(Date())
            if (storyId == null) {
                addStory(newStory, localImageURI)
            }else{
                updateStory(storyId, newStory, localImageURI)
            }
        }
    }

    private suspend fun updateStory(storyId: String, updatedStory: StoryEntity, localImageURI: Uri?){
        if (originalStoryTitle != updatedStory.name.value){
            // if the title was changed then need to check to make sure it is valid
            if (updatedStory.name.value in currentTitles!!){
                _duplicateTitleError.value = true
                return
            }

            // add this title to currentTitles
            currentTitles!!.add(updatedStory.name.value)
            //remove the title which is being replaced
            currentTitles!!.remove(originalStoryTitle)
        }
        Log.i(tag, "starting to update story")

        if (localImageURI != null){
            if (!localImageURI.toString().startsWith("https://firebasestorage.googleapis.com")){
                // if this was NOT the original image
                // trying to add a new image
                // no matter what adding the new image
                updatedStory.imageFilename.value = getStoryFilename(updatedStory.name.value)
                Log.i(tag, "adding new image to story with new filename: ${updatedStory.imageFilename.value}")
                val imageUrl = db.addImage(updatedStory.imageFilename.value!!, localImageURI)
                if(imageUrl != ""){
                    updatedStory.imagePublicUrl.value = imageUrl

                    //if adding a new image be sure to delete the original too (if it had one)
                    originalFilename?.let {
                            it1 ->
                        Log.i(tag, "deleting original story image with original filename: $it1")
                        db.deleteImage(it1)
                    }
                }else{
                    // if something went wrong with image upload
                    _uploadError.value = true
                    return
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
        val succeeded = db.updateStory(storyId, updatedStory, currentTitles)
        if(!succeeded && localImageURI != null){
            //failed and uploaded the image
            db.deleteImage(updatedStory.imageFilename.value!!)
            _uploadError.value = true
            return
        }else if (!succeeded){
            // if just failed
            _uploadError.value = true
            return
        }
        _navToStories.value = true
    }

    private suspend fun addStory(newStory: StoryEntity, localImageURI: Uri?){
        if (newStory.name.value in currentTitles!!){
            _duplicateTitleError.value = true
            return
        }

        // add this title to currentTitles
        currentTitles?.add(newStory.name.value)
        if (localImageURI != null) {
            // if we are adding an image
            newStory.imageFilename.value = getStoryFilename(newStory.name.value)
            val imageUrl = db.addImage(newStory.imageFilename.value!!, localImageURI)
            if (imageUrl != ""){
                newStory.imagePublicUrl.value = imageUrl
            }else{
                // if something went wrong with image upload
                _uploadError.value = true
                return
            }

        }
        Log.i(tag, "Creation of new story initiated")
        val succeeded = db.createStory(newStory, currentTitles!!)
        if (!succeeded && localImageURI != null){
            //failed and uploaded the image
            db.deleteImage(newStory.imageFilename.value!!)
            _uploadError.value = true
            return
        }else if (!succeeded){
            // if just failed
            _uploadError.value = true
            return
        }
        _navToStories.value = true
    }

    /* function which gets the story given the id*/
    private fun getStory(storyId: String){

        viewModelScope.launch {
            Log.d(tag, "storyID: $storyId")
            val t = db.getStoryFromId(storyId)
            _story.value = t
            originalFilename = story.value.imageFilename.value
            originalStoryTitle = story.value.name.value
        }
    }

    fun submitStoryDelete(){
        viewModelScope.launch {
            Log.i(tag, "starting to delete story")

            if (storyId != null) {
                db.deleteStory(storyId, currentTitles!!.filter { title -> title != originalStoryTitle })
            }
            // if it has an image delete that too
            story.value.imageFilename.value?.let { db.deleteImage(it) }
            _navToStories.value = true
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