package com.chartracker.viewmodels.story

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.StoryDB
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class StoriesViewModel : ViewModel(){
    private val _stories = MutableStateFlow<List<StoryEntity>>(emptyList())
    val stories: StateFlow<List<StoryEntity>> = _stories.asStateFlow()
    private val storyDB = StoryDB()

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
        Timber.tag("StoriesVM").i("getting stories")
        viewModelScope.launch {
            val temp = storyDB.getStories()
            if (temp == null){
                _failedGetStories.value = true
            }else{
                _stories.value = temp.sortedBy { storyEntity -> storyEntity.name.value }
            }
        }
    }

    fun storiesAlphaSort(){
        _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.name.value }
    }

    fun storiesReverseAlphaSort(){
        _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.name.value }.reversed()
    }

    fun storiesRecentSort(){
        _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.accessDate.value }.reversed()
    }

    fun storiesReverseRecentSort(){
        _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.accessDate.value }
    }
}