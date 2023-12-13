package com.chartracker.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddCharacterViewModelFactory(private val storyId: String, private val charList: List<String>): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AddCharacterViewModel::class.java)){
            return AddCharacterViewModel(storyId, charList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}