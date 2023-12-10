package com.chartracker.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class CharactersViewModelFactory(private val storyTitle: String): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CharactersViewModel::class.java)){
            return CharactersViewModel(storyTitle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}