package com.chartracker.characters

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class CharactersViewModel(private val storyTitle: String): ViewModel() {
    val characters = MutableLiveData<MutableList<CharacterEntity>>()
    val db = DatabaseAccess()

    init {
        //for testing purposes right now
        Log.i("CharVM", "story title: $storyTitle")
        viewModelScope.launch {
            characters.value = db.getCharacters("test")
        }
    }

    private val _charactersToEditStoryNavigate = MutableLiveData<Boolean>()
    val charactersToEditStoryNavigate: LiveData<Boolean>
        get() = _charactersToEditStoryNavigate

    fun onCharactersToEditStoryNavigate(){
        Log.i("CharVM", "nav from chars to edit story initiated")
        _charactersToEditStoryNavigate.value = true
    }

    fun onCharactersToEditStoryNavigateComplete(){
        Log.i("CharVM", "nav from chars to edit story completed")
        _charactersToEditStoryNavigate.value = false
    }
}