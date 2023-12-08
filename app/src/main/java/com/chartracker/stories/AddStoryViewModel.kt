package com.chartracker.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
import kotlinx.coroutines.launch

const val TAG = "AddStoryVM"
class AddStoryViewModel : ViewModel() {
    val db = DatabaseAccess()

    //navigation for after adding a story (or canceling)
    private val _addStoryNavigate = MutableLiveData<Boolean>()
    val addStoryNavigate: LiveData<Boolean>
        get() = _addStoryNavigate

    fun onAddStoryNavigate(){
        Log.i(TAG, "nav from add story back to stories initiated")
        _addStoryNavigate.value = true
    }

    fun onAddStoryNavigateComplete(){
        Log.i(TAG, "nav from add story back to stories completed")
        _addStoryNavigate.value = false
    }

    /*function that calls a database access method to create the story in Firebase
        also calls navigation*/
    fun submitStory(story: StoriesEntity){
        viewModelScope.launch {
            Log.i(TAG, "Creation of new story initiated")
            db.createStory(story)
            onAddStoryNavigate()
        }

    }
}