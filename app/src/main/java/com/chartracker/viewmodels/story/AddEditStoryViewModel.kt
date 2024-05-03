package com.chartracker.viewmodels.story

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.ImageDBInterface
import com.chartracker.database.StoryDBInterface
import com.chartracker.database.StoryEntity
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import java.util.Date

class AddEditStoryViewModel(private val storyId: String?, private val storyDB: StoryDBInterface, private val imageDB: ImageDBInterface): ViewModel() {
    private val tag = "AddEditStoryVM"
    private var originalFilename: MutableState<String?> = mutableStateOf(null)
    private var originalStoryTitle: MutableState<String> = mutableStateOf("")
    private var currentTitles: MutableList<String> = mutableListOf()

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

    /*retrieval error event*/
    private val _retrievalError = mutableStateOf(false)
    val retrievalError: MutableState<Boolean>
        get() = _retrievalError

    fun resetRetrievalError(){
        _retrievalError.value = false
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
            storyDB.getCurrentTitles(currentTitles, _uploadError)
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
        if (originalStoryTitle.value != updatedStory.name.value){
            // if the title was changed then need to check to make sure it is valid
            Timber.tag(tag).i("original title: ${originalStoryTitle.value} vs updated: ${updatedStory.name.value}")
            if (updatedStory.name.value in currentTitles){
                _duplicateTitleError.value = true
                return
            }

            // add this title to currentTitles
            currentTitles.add(updatedStory.name.value)
            //remove the title which is being replaced
            currentTitles.remove(originalStoryTitle.value)
        }
        Timber.tag(tag).i("starting to update story")

        if (localImageURI != null){
            if (!localImageURI.toString().startsWith("https://firebasestorage.googleapis.com")){
                // if this was NOT the original image
                // trying to add a new image
                // no matter what adding the new image
                updatedStory.imageFilename.value = getStoryFilename(updatedStory.name.value)
                Timber.tag(tag)
                    .i("adding new image to story with new filename: %s", updatedStory.imageFilename.value)
                try {
                    imageDB.addImage(updatedStory, localImageURI, _uploadError, originalFilename.value)
                }catch (exception: Exception){
                    return
                }

            }
            // if this was just the existing image don't do anything

        }else{
            // could be either making no image change or trying to delete it
            if (originalFilename.value != null) {
                // if there is no file in new version (due to localImageURI == null)
                //                  AND
                // the old version had one then we are deleting the current
                Timber.tag(tag)
                    .i("deleting original story image with original filename: %s", originalFilename.value)
                imageDB.deleteImage(originalFilename.value!!)
                updatedStory.imageFilename.value = null
                updatedStory.imagePublicUrl.value = null
            }
            // if both were null it would be that there started with and ended with no image
        }
        storyDB.updateStory(
            storyId,
            updatedStory,
            currentTitles,
            originalStoryTitle.value != updatedStory.name.value,
            _navToStories,
            _uploadError,
            localImageURI != null
        ) { imageDB.deleteImage(updatedStory.imageFilename.value!!) }
    }

    private suspend fun addStory(newStory: StoryEntity, localImageURI: Uri?){
        if (newStory.name.value in currentTitles){
            _duplicateTitleError.value = true
            return
        }

        // add this title to currentTitles
        currentTitles.add(newStory.name.value)
        if (localImageURI != null) {
            // if we are adding an image
            newStory.imageFilename.value = getStoryFilename(newStory.name.value)
            try {
                imageDB.addImage(newStory, localImageURI, _uploadError)
            }catch (exception: Exception){
                return
            }

        }
        Timber.tag(tag).i("Creation of new story initiated")
        storyDB.createStory(
            newStory,
            currentTitles,
            _navToStories,
            _uploadError,
            localImageURI != null
        ) { imageDB.deleteImage(newStory.imageFilename.value!!) }
    }

    /* function which gets the story given the id*/
    private fun getStory(storyId: String){

        viewModelScope.launch {
            Timber.tag(tag).d("storyID: %s", storyId)
            storyDB.getStoryFromId(storyId, _story, _retrievalError, originalFilename, originalStoryTitle)
        }
    }

    fun submitStoryDelete(){
        viewModelScope.launch {
            Timber.tag(tag).i("starting to delete story")

            if (storyId != null) {
                storyDB.deleteStory(storyId, currentTitles.filter { title -> title != originalStoryTitle.value })
            }
            // if it has an image delete that too
            story.value.imageFilename.value?.let { imageDB.deleteImage(it) }
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

class AddEditStoryViewModelFactory(private val storyId: String?, private val storyDB: StoryDBInterface, private val imageDB: ImageDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditStoryViewModel(storyId, storyDB, imageDB) as T
}