package com.chartracker.characters

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class AddCharacterViewModel(private val storyId: String, val charsList: List<String>): ViewModel() {
    private val tag = "AddCharVM"
    private val db = DatabaseAccess()

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



//    private val _charList =MutableLiveData<List<String>>()
//    val charList: LiveData<List<String>>
//        get() = _charList

    //navigation for after adding a story (or canceling)
    private val _addCharacterNavigate = MutableLiveData<Boolean>()
    val addCharacterNavigate: LiveData<Boolean>
        get() = _addCharacterNavigate

    private fun onAddCharacterNavigate(){
        Log.i(tag, "nav from add Character back to Characters initiated")
        _addCharacterNavigate.value = true
    }

    fun onAddCharacterNavigateComplete(){
        Log.i(tag, "nav from add Character back to Characters completed")
        _addCharacterNavigate.value = false
    }

    /*function that calls a database access method to create the character in Firebase
        also calls navigation*/
    fun submitCharacter(character: CharacterEntity, imageURI: String){
        viewModelScope.launch {
            Log.i(tag, "Creation of new char initiated")
            db.createCharacter(storyId, character)
            character.imageFilename?.let { db.addImage(it, imageURI.toUri()) }
            onAddCharacterNavigate()
        }
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