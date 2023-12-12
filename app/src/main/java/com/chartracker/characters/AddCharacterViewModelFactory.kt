package com.chartracker.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddCharacterViewModelFactory(private val storyId: String): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddCharacterViewModel::class.java)){
            return AddCharacterViewModel(storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}