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
    private val tag = "StoriesVM"
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
        Log.i(tag, "add story nav initiated")
        _addStoryNavigate.value = true
    }

    fun onAddStoryNavigateComplete(){
        Log.i(tag, "add story nav completed")
        _addStoryNavigate.value = false
    }

    // for navigating when clicked on a story
    private val _storyClickedNavigate = MutableLiveData<String?>()
    val storyClickedNavigate: LiveData<String?>
        get() = _storyClickedNavigate

    fun onStoryClickedNavigate(storyTitle: String){
        Log.i(tag, "story clicked nav initiated")
        _storyClickedNavigate.value = storyTitle
    }

    fun onStoryClickedNavigateComplete(){
        Log.i(tag, "story clicked nav completed")
        _storyClickedNavigate.value = null
    }

    private val _settingsNavigate = MutableLiveData<Boolean>()

    val settingsNavigate: LiveData<Boolean>
        get() = _settingsNavigate

    fun onSettingsNavigate(){
        Log.i("VM", "trying to nav to settings")
        _settingsNavigate.value = true
        Log.i("VM", "trying to nav to settings: ${_settingsNavigate.value}")
    }

    fun onSettingsNavigateComplete(){
        _settingsNavigate.value = false
    }

}