package com.chartracker.characters

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(val storyId: String, val storyTitle: String, val charName: String) : ViewModel() {
    private val tag = "CharDetailsVM"
    private val db = DatabaseAccess()

    var character = MutableLiveData<CharacterEntity>()
    lateinit var charId: String

    init {
        viewModelScope.launch {
            charId = db.getCharacterId(storyId,charName)
            character.value = db.getCharacterFromId(storyId, charId)
        }
    }

    //navigation for going to edit the character
    private val _charDetailsToEditCharNavigate = MutableLiveData<Boolean>()
    val charDetailsToEditCharNavigate: LiveData<Boolean>
        get() = _charDetailsToEditCharNavigate

    fun onCharDetailsToEditCharNavigate(){
        Log.i(tag, "nav from Character details to Character edit initiated")
        _charDetailsToEditCharNavigate.value = true
    }

    fun onCharDetailsToEditCharNavigateComplete(){
        Log.i(tag, "nav from Character details to Character edit completed")
        _charDetailsToEditCharNavigate.value = false
    }

    //navigation for returning to the characters
    private val _charDetailsToCharsNavigate = MutableLiveData<Boolean>()
    val charDetailsToCharsNavigate: LiveData<Boolean>
        get() = _charDetailsToCharsNavigate

    fun onCharDetailsToCharsNavigate(){
        Log.i(tag, "nav from Character details back to Characters initiated")
        _charDetailsToCharsNavigate.value = true
    }

    fun onCharDetailsToCharsNavigateComplete(){
        Log.i(tag, "nav from Character details back to Characters completed")
        _charDetailsToCharsNavigate.value = false
    }

    fun submitCharacterDelete(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(tag, "starting to delete character")
            db.deleteCharacter(storyId, charId, charName)

        }
        onCharDetailsToCharsNavigate()
    }
}