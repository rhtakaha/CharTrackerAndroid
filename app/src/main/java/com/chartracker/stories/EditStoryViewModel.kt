package com.chartracker.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.launch

class EditStoryViewModel(private val storyId: String): ViewModel() {
    var story = MutableLiveData<StoryEntity>()
    val db = DatabaseAccess()
    var filename = MutableLiveData<String?>()

    init {
        Log.i("EditStoryVM", " got story ID $storyId")
        viewModelScope.launch {
            story.value = db.getStoryFromId(storyId)
//            filename.value = story.value!!.imageFilename
        }
    }

    private val _editStoryToStoriesNavigate = MutableLiveData<Boolean>()
    val editStoryToStoriesNavigate: LiveData<Boolean>
        get() = _editStoryToStoriesNavigate

    private fun onEditStoryToStoriesNavigate(){
        _editStoryToStoriesNavigate.value = true
    }

    fun onEditStoryToStoriesNavigateComplete(){
        _editStoryToStoriesNavigate.value = false
    }

    private val _editStoryToCharactersNavigate = MutableLiveData<Boolean>()
    val editStoryToCharactersNavigate: LiveData<Boolean>
        get() = _editStoryToCharactersNavigate

    private fun onEditStoryToCharactersNavigate(){
        _editStoryToCharactersNavigate.value = true
    }

    fun onEditStoryToCharactersNavigateComplete(){
        _editStoryToCharactersNavigate.value = false
    }

    fun submitStoryUpdate(updatedStory: StoryEntity, imageURI: String){
        viewModelScope.launch {
            Log.i("EditStoryVM", "starting to update story")
            db.updateStory(storyId, updatedStory)

            if (imageURI != ""){
                // trying to add a new image
                //if adding a new image be sure to delete the original too (if it had one)
//                updatedStory.imageFilename?.let {
//                    db.addImage(it, imageURI.toUri())
//                    story.value!!.imageFilename?.let { it1 -> db.deleteImage(it1) }
//                }
            }else{
                // could be either making no image change or trying to delete it
//                if(updatedStory.imageFilename == null && story.value!!.imageFilename != null){
//                    // if there is no filename listed in new version
//                    //                  AND
//                    // the old version had one then we are deleting the current
//                    db.deleteImage(story.value!!.imageFilename!!)
//                }
                // if both were null it would be that there started with and ended with no image
            }

            onEditStoryToCharactersNavigate()
        }
    }

    fun submitStoryDelete(){
        viewModelScope.launch {
            Log.i("EditStoryVM", "starting to delete story")
            db.deleteStory(storyId)
            // if it has an image delete that too
//            story.value!!.imageFilename?.let { db.deleteImage(it) }
            onEditStoryToStoriesNavigate()
        }
    }

    private val _settingsNavigate = MutableLiveData<Boolean>()

    val settingsNavigate: LiveData<Boolean>
        get() = _settingsNavigate

    fun onSettingsNavigate(){
        Log.i("VM", "trying to nav to settings")
        _settingsNavigate.value = true
        Log.i("VM", "trying to nav to settings: ${_settingsNavigate.value}")
    }

    fun onSettingsNavigateComplete(){
        _settingsNavigate.value = false
    }
}