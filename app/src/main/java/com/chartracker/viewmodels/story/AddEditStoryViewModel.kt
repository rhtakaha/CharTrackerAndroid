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
    private val tag = "AddStoryVM"
    private val db = DatabaseAccess()

    init {
        if (storyId != null) {
            getStory(storyId= storyId)
        }
    }

    private val _story = mutableStateOf<StoryEntity?>(null)
    val story: MutableState<StoryEntity?>
        get() = _story

    private fun updateStory(newStory: StoryEntity){
        _story.value = newStory
    }

    private val _title = mutableStateOf("")
    val title: MutableState<String>
        get() = _title

    fun updateInputTitle(newTitle: String){
        _title.value = newTitle
    }

    private val _author = mutableStateOf("")
    val author: MutableState<String>
        get() = _author

    fun updateInputAuthor(newAuthor: String){
        _author.value = newAuthor
    }

    private val _genre = mutableStateOf("")
    val genre: MutableState<String>
        get() = _genre

    fun updateInputGenre(newGenre: String){
        _genre.value = newGenre
    }

    private val _type = mutableStateOf("")
    val type: MutableState<String>
        get() = _type

    fun updateInputType(newType: String){
        _type.value = newType
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
        }
    }

    private suspend fun updateStory(storyId: String, updatedStory: StoryEntity, localImageURI: Uri?){
        Log.i(tag, "starting to update story")


        if (localImageURI != null){
            // trying to add a new image
            // no matter what adding the new image
            updatedStory.imageFilename = getFilename(updatedStory.name!!)
            db.addImage(updatedStory.imageFilename!!, localImageURI)
            db.addImageDownloadUrlToStory(updatedStory, updatedStory.imageFilename!!)

            //if adding a new image be sure to delete the original too (if it had one)
            story.value!!.imagePublicUrl?.let {
                db.deleteImage(story.value!!.imageFilename!!)

            }
//                updatedStory.imageFilename?.let {
//                    db.addImage(it, imageURI.toUri())
//                    story.value!!.imageFilename?.let { it1 -> db.deleteImage(it1) }
//                }
        }else{
            // could be either making no image change or trying to delete it
            if (updatedStory.imageFilename == null && story.value!!.imageFilename != null) {
                // if there is no filename listed in new version
                //                  AND
                // the old version had one then we are deleting the current
                db.deleteImage(story.value!!.imageFilename!!)
            }
            // if both were null it would be that there started with and ended with no image
        }
        db.updateStory(storyId, updatedStory)
        _navToStories.value = true
    }

    private suspend fun addStory(newStory: StoryEntity, localImageURI: Uri?){
        if (localImageURI != null) {
            // if we are adding an image
            newStory.imageFilename = getFilename(newStory.name!!)
            db.addImage(newStory.imageFilename!!, localImageURI)
            db.addImageDownloadUrlToStory(newStory, newStory.imageFilename!!)
        }
        Log.i(tag, "Creation of new story initiated")
        db.createStory(newStory)
        _navToStories.value = true
    }

    /* function which gets the story given the id*/
    private fun getStory(storyId: String){
        viewModelScope.launch {
            updateStory(db.getStoryFromId(storyId))
            story.value?.name?.let { updateInputTitle(it) }
            story.value?.author?.let { updateInputAuthor(it) }
            story.value?.genre?.let { updateInputGenre(it) }
            story.value?.type?.let { updateInputType(it) }

            //TODO ADD IMAGE
            // add factory so can call in init{} block
            // add navigation arg
//            filename.value = story.value!!.imageFilename
        }
    }
}

/* time ensures that filenames are unique:
            can't go back in time even if you add story w/ image,
                change that story's name, (image keeps old name),
                and then try to add a story with the original story name
                    (which would have created two images with the same name)*/
fun getFilename(title: String) = "story_${title}_{${Calendar.getInstance().time}}"

class AddEditStoryViewModelFactory(private val storyId: String?) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = AddEditStoryViewModel(storyId) as T
}