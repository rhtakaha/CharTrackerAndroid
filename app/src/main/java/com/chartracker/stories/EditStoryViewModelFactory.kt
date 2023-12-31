package com.chartracker.stories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class EditStoryViewModelFactory(private val storyId: String): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditStoryViewModel::class.java)){
            return EditStoryViewModel(storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}