package com.chartracker.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EditCharacterViewModelFactory(private val storyId: String, private val charId: String, private val charsList: List<String>): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditCharacterViewModel::class.java)){
            return EditCharacterViewModel(storyId, charId, charsList) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}