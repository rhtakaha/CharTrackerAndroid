package com.chartracker.stories


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoriesEntity
import kotlinx.coroutines.launch


class StoriesViewModel : ViewModel() {
    val stories = MutableLiveData<MutableList<StoriesEntity>>()
    val db = DatabaseAccess()

    init {
        //for testing purposes right now
//        stories.value = mutableListOf()
////        stories.value?.add(StoriesEntity("Lord of the Rings"))
////        stories.value?.add(StoriesEntity("Ender's Game"))
        viewModelScope.launch {
            stories.value = db.getStories()
        }
    }

    //for navigating when adding a story
    private val _addStoryNavigate = MutableLiveData<Boolean>()
    val addStoryNavigate: LiveData<Boolean>
        get() = _addStoryNavigate

    fun onAddStoryNavigate(){
        Log.i(com.chartracker.auth.TAG, "add story nav initiated")
        _addStoryNavigate.value = true
    }

    fun onAddStoryNavigateComplete(){
        Log.i(com.chartracker.auth.TAG, "add story nav completed")
        _addStoryNavigate.value = false
    }

    // for navigating when to a story
    private val _storyClickedNavigate = MutableLiveData<String?>()
    val storyClickedNavigate: LiveData<String?>
        get() = _storyClickedNavigate

    fun onStoryClickedNavigate(storyTitle: String){
        Log.i(com.chartracker.auth.TAG, "story clicked nav initiated")
        _storyClickedNavigate.value = storyTitle
    }

    fun onStoryClickedNavigateComplete(){
        Log.i(com.chartracker.auth.TAG, "story clicked nav completed")
        _storyClickedNavigate.value = null
    }
}