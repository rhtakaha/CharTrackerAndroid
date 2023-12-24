package com.chartracker.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
import kotlinx.coroutines.launch

class EditStoryViewModel(private val storyId: String): ViewModel() {
    var story = MutableLiveData<StoriesEntity>()
    val db = DatabaseAccess()

    init {
        Log.i("EditStoryVM", " got story ID $storyId")
        viewModelScope.launch {
            story.value = db.getStoryFromId(storyId)
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

    fun submitStoryUpdate(story: StoriesEntity){
        viewModelScope.launch {
            Log.i("EditStoryVM", "starting to update story")
            db.updateStory(storyId, story)
            onEditStoryToCharactersNavigate()
        }
    }

    fun submitStoryDelete(){
        viewModelScope.launch {
            Log.i("EditStoryVM", "starting to delete story")
            db.deleteStory(storyId)
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