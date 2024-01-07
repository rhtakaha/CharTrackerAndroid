package com.chartracker.stories

import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
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
    fun submitStory(story: StoriesEntity, imageURI: String, imageType: String){

        viewModelScope.launch {
            Log.i(tag, "Creation of new story initiated")
            db.createStory(story)
            if(imageURI != "" && imageType != ""){
                val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(imageType)
                db.addImage("${story.title}.$extension", imageURI.toUri())
            }
            onAddStoryNavigate()
        }
    }
}