package com.chartracker.characters

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCharacterViewModel(val storyId: String, val charId: String, val charsList: List<String>): ViewModel() {
    val tag = "EditCharVM"
    val character = MutableLiveData<CharacterEntity>()
    val db = DatabaseAccess()
    var filename = MutableLiveData<String?>()

    init {
        viewModelScope.launch {
            character.value = db.getCharacterFromId(storyId, charId)
            filename.value = character.value!!.imageFilename.value
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

    fun submitCharacterUpdate(updatedCharacter: CharacterEntity, imageURI: String){
        viewModelScope.launch {
            Log.i("EditCharVM", "starting to update character")
            db.updateCharacter(storyId, charId, updatedCharacter)

            if (imageURI != ""){
                // trying to add a new image
                //if adding a new image be sure to delete the original too (if it had one)
                updatedCharacter.imageFilename.value?.let {
                    db.addImage(it, imageURI.toUri())
                    character.value!!.imageFilename.value?.let { it1 -> db.deleteImage(it1) }
                }
            }else{
                // could be either making no image change or trying to delete it
                if(updatedCharacter.imageFilename.value == null && character.value!!.imageFilename.value != null){
                    // if there is no filename listed in new version
                    //                  AND
                    // the old version had one then we are deleting the current
                    db.deleteImage(character.value!!.imageFilename.value!!)
                }
                // if both were null it would be that there started with and ended with no image
            }

            onEditCharacterToCharacterDetailsNavigate()
        }
    }

    fun submitCharacterDelete(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(tag, "starting to delete character")
            character.value!!.name.value?.let { db.deleteCharacter(storyId, charId, it) }
            character.value!!.imageFilename.value?.let { db.deleteImage(it) }
        }
        onEditCharacterToCharactersNavigate()
    }

    private val _editCharacterToCharactersNavigate = MutableLiveData<Boolean>()
    val editCharacterToCharactersNavigate: LiveData<Boolean>
        get() = _editCharacterToCharactersNavigate

    private fun onEditCharacterToCharactersNavigate(){
        _editCharacterToCharactersNavigate.value = true
    }

    fun onEditCharacterToCharactersNavigateComplete(){
        _editCharacterToCharactersNavigate.value = false
    }


    private val _editCharacterToCharacterDetailsNavigate = MutableLiveData<Boolean>()
    val editCharacterToCharacterDetailsNavigate: LiveData<Boolean>
        get() = _editCharacterToCharacterDetailsNavigate

    private fun onEditCharacterToCharacterDetailsNavigate(){
        Log.i("EditCharVM", "nav from edit character back to character details initiated")
        _editCharacterToCharacterDetailsNavigate.value = true
    }

    fun onEditCharacterToCharacterDetailsNavigateComplete(){
        Log.i("EditCharVM", "nav from edit character back to character details completed")
        _editCharacterToCharacterDetailsNavigate.value = false
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