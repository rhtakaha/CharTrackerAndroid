package com.chartracker.characters

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class AddCharacterViewModel(private val storyId: String): ViewModel() {
    private val tag = "AddCharVM"
    private val db = DatabaseAccess()

    //navigation for after adding a story (or canceling)
    private val _addCharacterNavigate = MutableLiveData<Boolean>()
    val addCharacterNavigate: LiveData<Boolean>
        get() = _addCharacterNavigate

    fun onAddCharacterNavigate(){
        Log.i(tag, "nav from add Character back to Characters initiated")
        _addCharacterNavigate.value = true
    }

    fun onAddCharacterNavigateComplete(){
        Log.i(tag, "nav from add Character back to Characters completed")
        _addCharacterNavigate.value = false
    }

    /*function that calls a database access method to create the character in Firebase
        also calls navigation*/
    fun submitCharacter(character: CharacterEntity){
        viewModelScope.launch {
            Log.i(tag, "Creation of new char initiated")
            db.createCharacter(storyId, character)
            onAddCharacterNavigate()
        }
    }
}