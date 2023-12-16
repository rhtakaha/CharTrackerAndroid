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
    val charactersStringList = mutableListOf<String>()
    val db = DatabaseAccess()
    lateinit var storyId: String

    val tag = "CharVM"
    init {
        //for testing purposes right now
        Log.i("CharVM", "story title: $storyTitle")
        viewModelScope.launch {
            storyId = db.getStoryId(storyTitle)
            Log.i("CharVM", "doc ID is: $storyId")
            characters.value = db.getCharacters(storyId)
        }
    }

    /* function to update the list of all the character names (as Strings)
        which we will pass to edit/add Character*/
    fun updateCharsStringList(){
        charactersStringList.clear()
        for (character in characters.value!!){
            character.name?.let { charactersStringList.add(it) }
        }
    }

    private val _charactersToEditStoryNavigate = MutableLiveData<Boolean>()
    val charactersToEditStoryNavigate: LiveData<Boolean>
        get() = _charactersToEditStoryNavigate

    fun onCharactersToEditStoryNavigate(){
        Log.i(tag, "nav from chars to edit story initiated")
        _charactersToEditStoryNavigate.value = true
    }

    fun onCharactersToEditStoryNavigateComplete(){
        Log.i(tag, "nav from chars to edit story completed")
        _charactersToEditStoryNavigate.value = false
    }

    private val _charactersToAddCharacterNavigate = MutableLiveData<Boolean>()
    val charactersToAddCharacterNavigate: LiveData<Boolean>
        get() = _charactersToAddCharacterNavigate

    fun onCharactersToAddCharacterNavigate(){
        Log.i(tag, "nav from chars to add chars initiated")
        _charactersToAddCharacterNavigate.value = true
    }

    fun onCharactersToAddCharacterNavigateComplete(){
        Log.i(tag, "nav from chars to add chars completed")
        _charactersToAddCharacterNavigate.value = false
    }

    // for navigating when clicked on a character
    private val _characterClickedNavigate = MutableLiveData<String?>()
    val characterClickedNavigate: LiveData<String?>
        get() = _characterClickedNavigate

    fun onCharacterClickedNavigate(charName: String){
        Log.i(tag, "character clicked nav initiated")
        _characterClickedNavigate.value = charName
    }

    fun onCharacterClickedNavigateComplete(){
        Log.i(tag, "character clicked nav completed")
        _characterClickedNavigate.value = null
    }
}