package com.example.chartracker.stories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chartracker.database.StoriesEntity

class StoriesViewModel : ViewModel() {
    val stories = MutableLiveData<MutableList<StoriesEntity>>()

    init {
        //for testing purposes right now
        stories.value = mutableListOf()
        stories.value?.add(StoriesEntity("Lord of the Rings"))
        stories.value?.add(StoriesEntity("Ender's Game"))
    }
}