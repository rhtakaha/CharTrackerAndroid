package com.chartracker.stories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity

class EditStoryViewModel(private val storyTitle: String): ViewModel() {
    var story = MutableLiveData<StoriesEntity>()
    val db = DatabaseAccess()

    //TODO finish after refactoring CharactersVieModel and EditStoryViewModel with the factory pattern so we can access the arguments
    init {
        Log.i("EditStoryVM", " got story title $storyTitle")
//        viewModelScope.launch {
//            story.value = db.getStory()
//        }
    }

    private val _editStoryNavigate = MutableLiveData<Boolean>()
    val editStoryNavigate: LiveData<Boolean>
        get() = _editStoryNavigate

    fun onEditStoryNavigate(){
        Log.i("EditStoryVM", "nav from edit story back to stories initiated")
        _editStoryNavigate.value = true
    }

    fun onEditStoryNavigateComplete(){
        Log.i("EditStoryVM", "nav from edit story back to stories completed")
        _editStoryNavigate.value = false
    }
}