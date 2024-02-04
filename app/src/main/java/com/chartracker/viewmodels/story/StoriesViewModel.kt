package com.chartracker.viewmodels.story

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoriesViewModel : ViewModel(){
    private val _stories = MutableStateFlow<List<StoryEntity>>(emptyList())
    val stories: StateFlow<List<StoryEntity>> = _stories.asStateFlow()
    val db = DatabaseAccess()

    init {
        getStories()
    }
    fun getStories(){
        Log.i("StoriesVM", "getting stories")
        viewModelScope.launch {
            _stories.value = db.getStories()
        }
    }
}