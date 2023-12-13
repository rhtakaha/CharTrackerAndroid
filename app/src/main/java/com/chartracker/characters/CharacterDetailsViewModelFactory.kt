package com.chartracker.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CharacterDetailsViewModelFactory(private val storyId: String, private val storyTitle: String, private val charName: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CharacterDetailsViewModel::class.java)){
            return CharacterDetailsViewModel(storyId, storyTitle, charName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}