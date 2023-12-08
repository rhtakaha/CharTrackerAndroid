package com.chartracker.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val TAG = "AddStoryVM"
class AddStoryViewModel : ViewModel() {

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
    fun submitStory(title: String, genre: String, type: String, author: String){
        Log.i(TAG, "Creation of new story initiated")
        onAddStoryNavigate()
    }
}