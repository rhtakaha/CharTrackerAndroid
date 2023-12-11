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

    fun submitStoryUpdate(story: StoriesEntity){
        viewModelScope.launch {
            Log.i("EditStoryVM", "starting to update story")
            db.updateStory(storyId, story)
            onEditStoryNavigate()
        }
    }
}