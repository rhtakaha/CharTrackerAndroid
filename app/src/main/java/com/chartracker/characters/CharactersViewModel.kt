package com.chartracker.characters

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

open class CharactersViewModel(private val storyTitle: String): ViewModel() {
    val characters = MutableLiveData<MutableList<CharacterEntity>>()
    val db = DatabaseAccess()
    lateinit var storyId: String

    init {
        //for testing purposes right now
        Log.i("CharVM", "story title: $storyTitle")
        viewModelScope.launch {
            storyId = db.getDocId(storyTitle)
            Log.i("CharVM", "doc ID is: $storyId")
            characters.value = db.getCharacters(storyId)
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

    private val _charactersToAddCharacterNavigate = MutableLiveData<Boolean>()
    val charactersToAddCharacterNavigate: LiveData<Boolean>
        get() = _charactersToAddCharacterNavigate

    fun onCharactersToAddCharacterNavigate(){
        Log.i("CharVM", "nav from chars to add chars initiated")
        _charactersToAddCharacterNavigate.value = true
    }

    fun onCharactersToAddCharacterNavigateComplete(){
        Log.i("CharVM", "nav from chars to add chars completed")
        _charactersToAddCharacterNavigate.value = false
    }
}