package com.chartracker.stories


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
            stories.value = db.getStory()
        }

    }
}