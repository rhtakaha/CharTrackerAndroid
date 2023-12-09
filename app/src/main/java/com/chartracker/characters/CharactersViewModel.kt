package com.chartracker.characters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class CharactersViewModel : ViewModel() {
    val characters = MutableLiveData<MutableList<CharacterEntity>>()
    val db = DatabaseAccess()

    init {
        //for testing purposes right now
        viewModelScope.launch {
            characters.value = db.getCharacters("test")
        }
    }
}