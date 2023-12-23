package com.chartracker.characters

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class EditCharacterViewModel(val storyId: String, val charId: String, val charsList: List<String>): ViewModel() {
    val character = MutableLiveData<CharacterEntity>()
    val db = DatabaseAccess()

    init {
        viewModelScope.launch {
            character.value = db.getCharacterFromId(storyId, charId)
        }
    }

    /* Since this is the same as in AddCharacterViewModel perhaps could make it part of a superclass?*/
    val allies = mutableListOf<String>()

    fun alliesUpdated(charName: String, selected: Boolean){
        if (selected){
            //if selected add
            allies.add(charName)
        }else{
            // if unselected then remove
            allies.remove(charName)
        }
    }

    val enemies = mutableListOf<String>()

    fun enemiesUpdated(charName: String, selected: Boolean){
        if (selected){
            //if selected add
            enemies.add(charName)
        }else{
            // if unselected then remove
            enemies.remove(charName)
        }
    }

    val neutral = mutableListOf<String>()

    fun neutralsUpdated(charName: String, selected: Boolean){
        if (selected){
            //if selected add
            neutral.add(charName)
        }else{
            // if unselected then remove
            neutral.remove(charName)
        }
    }

    fun submitCharacterUpdate(character: CharacterEntity){
        viewModelScope.launch {
            Log.i("EditCharVM", "starting to update character")
            db.updateCharacter(storyId, charId, character)
            onEditCharacterNavigate()
        }
    }


    private val _editCharacterNavigate = MutableLiveData<Boolean>()
    val editCharacterNavigate: LiveData<Boolean>
        get() = _editCharacterNavigate

    fun onEditCharacterNavigate(){
        Log.i("EditCharVM", "nav from edit character back to character details initiated")
        _editCharacterNavigate.value = true
    }

    fun onEditCharacterNavigateComplete(){
        Log.i("EditCharVM", "nav from edit character back to character details completed")
        _editCharacterNavigate.value = false
    }

    private val _settingsNavigate = MutableLiveData<Boolean>()

    val settingsNavigate: LiveData<Boolean>
        get() = _settingsNavigate

    fun onSettingsNavigate(){
        Log.i("VM", "trying to nav to settings")
        _settingsNavigate.value = true
        Log.i("VM", "trying to nav to settings: ${_settingsNavigate.value}")
    }

    fun onSettingsNavigateComplete(){
        _settingsNavigate.value = false
    }
}