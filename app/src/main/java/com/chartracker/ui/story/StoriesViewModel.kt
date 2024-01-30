package com.chartracker.ui.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoriesViewModel : ViewModel(){
    private val _stories = MutableStateFlow<List<StoriesEntity>>(emptyList())
    val stories: StateFlow<List<StoriesEntity>> = _stories.asStateFlow()
    val db = DatabaseAccess()

    init {
        getStories()
    }

    fun getStories(){
        viewModelScope.launch {
            _stories.value = db.getStories()
        }
    }
}