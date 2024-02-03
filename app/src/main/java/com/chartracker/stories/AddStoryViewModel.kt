package com.chartracker.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.launch


class AddStoryViewModel : ViewModel() {
    private val tag = "AddStoryVM"
    private val db = DatabaseAccess()

    //navigation for after adding a story (or canceling)
    private val _addStoryNavigate = MutableLiveData<Boolean>()
    val addStoryNavigate: LiveData<Boolean>
        get() = _addStoryNavigate

    private fun onAddStoryNavigate(){
        Log.i(tag, "nav from add story back to stories initiated")
        _addStoryNavigate.value = true
    }

    fun onAddStoryNavigateComplete(){
        Log.i(tag, "nav from add story back to stories completed")
        _addStoryNavigate.value = false
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

    /*function that calls a database access method to create the story in Firebase
        also calls navigation*/
    fun submitStory(story: StoryEntity, imageURI: String){

        viewModelScope.launch {
            Log.i(tag, "Creation of new story initiated")
            db.createStory(story)
//            story.imageFilename?.let { db.addImage(it, imageURI.toUri()) }
            onAddStoryNavigate()
        }
    }
}