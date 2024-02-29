package com.chartracker.viewmodels.story

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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

    /* event for failing to get stories*/
    private val _failedGetStories = mutableStateOf(false)
    val failedGetStories: MutableState<Boolean>
        get() = _failedGetStories

    fun resetFailedGetStories(){
        _failedGetStories.value = false
    }

    init {
        getStories()
    }
    fun getStories(){
        Log.i("StoriesVM", "getting stories")
        viewModelScope.launch {
            val temp = db.getStories()
            if (temp == null){
                _failedGetStories.value = true
            }else{
                _stories.value = temp
            }
        }
    }
}