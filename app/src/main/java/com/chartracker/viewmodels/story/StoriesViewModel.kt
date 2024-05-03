package com.chartracker.viewmodels.story

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.StoryDBInterface
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class StoriesViewModel(private val storyDB: StoryDBInterface) : ViewModel(){
    private val _stories = MutableStateFlow<MutableList<StoryEntity>>(mutableListOf())
    val stories: StateFlow<List<StoryEntity>> = _stories.asStateFlow()

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
            storyDB.getStories(_stories, _failedGetStories)
            _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.name.value }.toMutableList()
        }
    }

    fun storiesAlphaSort(){
        _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.name.value }.toMutableList()
    }

    fun storiesReverseAlphaSort(){
        _stories.value =
            _stories.value.sortedBy { storyEntity -> storyEntity.name.value }.reversed().toMutableList()
    }

    fun storiesRecentSort(){
        _stories.value =
            _stories.value.sortedBy { storyEntity -> storyEntity.accessDate.value }.reversed().toMutableList()
    }

    fun storiesReverseRecentSort(){
        _stories.value = _stories.value.sortedBy { storyEntity -> storyEntity.accessDate.value }.toMutableList()
    }
}

class StoriesViewModelFactory(private val storyDB: StoryDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        StoriesViewModel(storyDB) as T
}