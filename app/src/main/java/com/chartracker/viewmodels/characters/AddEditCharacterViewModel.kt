package com.chartracker.viewmodels.characters

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch
import java.util.Calendar

class AddEditCharacterViewModel(private val storyId: String, private val storyTitle: String, private val charId: String?): ViewModel() {
    private val tag = "AddEditCharVM"
    private val db = DatabaseAccess()
    private var originalFilename: String? = null

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    fun updateLocalCharacter(new: CharacterEntity){
        _character.value = new
    }

    /*navigate back to characters event*/
    private val _readyToNavToCharacters = mutableStateOf(false)
    val readyToNavToCharacters: MutableState<Boolean>
        get() = _readyToNavToCharacters

    fun resetReadyToNavToCharacters(){
        _readyToNavToCharacters.value = false
    }

    init {
        if (charId != null){
            getCharacter(charId)
        }
    }

    private fun getCharacter(charId: String){
        viewModelScope.launch {
            updateLocalCharacter(db.getCharacter(storyId, charId))
            originalFilename = character.value.imageFilename.value
        }
    }

    /*function that calls a database access method to create the character in Firebase
        also calls navigation*/
    fun submitCharacter(newCharacter: CharacterEntity, localImageURI: Uri?){
        viewModelScope.launch {
            if (charId == null){
                addCharacter(storyId, newCharacter, localImageURI)
            }else{
                updateCharacter(newCharacter, localImageURI, charId)
            }
            _readyToNavToCharacters.value = true
        }
    }

    private suspend fun addCharacter(storyId: String, newCharacter: CharacterEntity, localImageURI: Uri?){
        Log.i(tag, "Creation of new char initiated")
        if (localImageURI != null){
            // adding an image
            newCharacter.imageFilename.value = getCharacterFilename(newCharacter.name.value,
                storyTitle
            )
            db.addImage(newCharacter.imageFilename.value!!, localImageURI)
            db.addImageDownloadUrlToCharacter(newCharacter, newCharacter.imageFilename.value!!)
        }
        db.createCharacter(storyId, newCharacter)
    }

    private suspend fun updateCharacter(updatedCharacter: CharacterEntity, localImageURI: Uri?, charId: String){
        Log.i("EditCharVM", "starting to update character")
        if (localImageURI != null){
            // trying to add a new image
            //if adding a new image be sure to delete the original too (if it had one)
            updatedCharacter.imageFilename.value = getCharacterFilename(updatedCharacter.name.value, storyTitle)
            db.addImage(updatedCharacter.imageFilename.value!!, localImageURI)
            db.addImageDownloadUrlToCharacter(updatedCharacter, updatedCharacter.imageFilename.value!!)

            //if adding a new image be sure to delete the original too (if it had one)
            originalFilename?.let { it1 -> db.deleteImage(it1) }
        }else{
            // could be either making no image change or trying to delete it
            if(originalFilename != null){
                // if there is no file in new version (due to localImageURI == null)
                //                  AND
                // the old version had one then we are deleting the current
                db.deleteImage(originalFilename!!)
                updatedCharacter.imageFilename.value = null
                updatedCharacter.imagePublicUrl.value = null
            }
            // if both were null it would be that there started with and ended with no image
        }
        db.updateCharacter(storyId, charId, updatedCharacter)
    }


}

/* time ensures that filenames are unique:
            can't go back in time even if you add character w/ image,
                change that story's character, (image keeps old name),
                and then try to add a character with the original character name
                    (which would have created two images with the same name)*/
fun getCharacterFilename(name: String, title: String) = "char_${name}_story_${title}_${Calendar.getInstance().time}"

class AddEditCharacterViewModelFactory(private val storyId: String, private val storyTitle: String, private val charId: String?) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddEditCharacterViewModel(storyId, storyTitle, charId) as T
}