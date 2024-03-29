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
import com.chartracker.database.ImageDBInterface
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddEditCharacterViewModel(private val storyId: String, private val storyTitle: String, charName: String?, private val imageDB: ImageDBInterface): ViewModel() {
    private val tag = "AddEditCharVM"
    private val db = DatabaseAccess()
    private var originalFilename: String? = null
    private lateinit var originalCharacterName: String
    private var charId: String? = null
    private var currentNames: MutableList<String>? = null

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    private var _charactersStringList: MutableList<String> = mutableListOf()
    val charactersStringList: List<String>
        get() = _charactersStringList

    private val alliesList = mutableListOf<String>()
    private val enemiesList = mutableListOf<String>()
    private val neutralList = mutableListOf<String>()

    /*navigate back to characters event*/
    private val _readyToNavToCharacters = mutableStateOf(false)
    val readyToNavToCharacters: MutableState<Boolean>
        get() = _readyToNavToCharacters

    fun resetReadyToNavToCharacters(){
        _readyToNavToCharacters.value = false
    }

    /*upload error event*/
    private val _uploadError = mutableStateOf(false)
    val uploadError: MutableState<Boolean>
        get() = _uploadError

    fun resetUploadError(){
        _uploadError.value = false
    }

    /*retrieval error event*/
    private val _retrievalError = mutableStateOf(false)
    val retrievalError: MutableState<Boolean>
        get() = _retrievalError

    fun resetRetrievalError(){
        _retrievalError.value = false
    }

    /*duplicate name error event*/
    private val _duplicateNameError = mutableStateOf(false)
    val duplicateNameError: MutableState<Boolean>
        get() = _duplicateNameError

    fun resetDuplicateNameError(){
        _duplicateNameError.value = false
    }

    init {
        viewModelScope.launch {
            currentNames = db.getCurrentNames(storyId)
            if (currentNames == null){
                _retrievalError.value = true
                return@launch
            }
            if (charName != null){
                getCharacter(charName)
            }
        }
    }

    private suspend fun getCharacter(charName: String){
        charId = db.getCharacterId(storyId, charName)
        if (charId != ""){
            _character.value = db.getCharacter(storyId, charName)
            if (_character.value.name.value != ""){
                originalCharacterName = character.value.name.value
                originalFilename = character.value.imageFilename.value
                _charactersStringList = currentNames!!.filter { name -> name != charName }.toMutableList()

            }else{
                _retrievalError.value = true
            }
        }else{
            _retrievalError.value = true
        }


    }

    /*function that calls a database access method to create the character in Firebase
        also calls navigation*/
    fun submitCharacter(newCharacter: CharacterEntity, localImageURI: Uri?){
        newCharacter.accessDate.value = Timestamp(Date())
        if (alliesList.size > 0){
            newCharacter.allies.value = alliesList
        }
        if(enemiesList.size > 0){
            newCharacter.enemies.value = enemiesList
        }
        if (neutralList.size > 0){
            newCharacter.neutral.value = neutralList
        }
        viewModelScope.launch {
            if (charId == null){
                addCharacter(storyId, newCharacter, localImageURI)
            }else{
                updateCharacter(newCharacter, localImageURI, charId!!)
            }
        }
    }

    private suspend fun addCharacter(storyId: String, newCharacter: CharacterEntity, localImageURI: Uri?){
        Log.i(tag, "Creation of new char initiated")
        if (newCharacter.name.value in currentNames!!){
            _duplicateNameError.value = true
            return
        }

        // add the new name to the list
        currentNames!!.add(newCharacter.name.value)

        if (localImageURI != null){
            // adding an image
            newCharacter.imageFilename.value = getCharacterFilename(newCharacter.name.value, storyTitle)
            val imageUrl = imageDB.addImage(newCharacter.imageFilename.value!!, localImageURI)
            if (imageUrl != ""){
                newCharacter.imagePublicUrl.value = imageUrl
            }else{
                _uploadError.value = true
                return
            }

        }
        val succeeded = db.createCharacter(storyId, newCharacter, currentNames!!)
        if (!succeeded && localImageURI != null){
            //failed and uploaded the image
            imageDB.deleteImage(newCharacter.imageFilename.value!!)
            _uploadError.value = true
            return
        }else if (!succeeded){
            // if just failed
            _uploadError.value = true
            return
        }
        _readyToNavToCharacters.value = true
    }

    private suspend fun updateCharacter(updatedCharacter: CharacterEntity, localImageURI: Uri?, charId: String){
        Log.i("EditCharVM", "starting to update character")
        if (updatedCharacter.name.value != originalCharacterName){
            // if the name was changed have to make sure it is valid
            if (updatedCharacter.name.value in currentNames!!){
                _duplicateNameError.value = true
                return
            }

            // add the new name to the list
            currentNames!!.add(updatedCharacter.name.value)
            // remove the name which is being replaced
            currentNames!!.remove(originalCharacterName)
        }

        if (localImageURI != null){
            if (!localImageURI.toString().startsWith("https://firebasestorage.googleapis.com")){
                // if this was NOT the original image
                // trying to add a new image
                updatedCharacter.imageFilename.value = getCharacterFilename(updatedCharacter.name.value, storyTitle)

                val imageUrl = imageDB.addImage(updatedCharacter.imageFilename.value!!, localImageURI)
                if (imageUrl != ""){
                    updatedCharacter.imagePublicUrl.value = imageUrl

                    //if adding a new image be sure to delete the original too (if it had one)
                    originalFilename?.let { it1 -> imageDB.deleteImage(it1) }
                }else{
                    _uploadError.value = true
                    return
                }
            }
            // if this was just the existing image don't do anything

        }else{
            // could be either making no image change or trying to delete it
            if(originalFilename != null){
                // if there is no file in new version (due to localImageURI == null)
                //                  AND
                // the old version had one then we are deleting the current
                imageDB.deleteImage(originalFilename!!)
                updatedCharacter.imageFilename.value = null
                updatedCharacter.imagePublicUrl.value = null
            }
            // if both were null it would be that there started with and ended with no image
        }

        val succeeded = db.updateCharacter(storyId, charId, updatedCharacter, currentNames)
        if (!succeeded && localImageURI != null){
            //failed and uploaded the image
            imageDB.deleteImage(updatedCharacter.imageFilename.value!!)
            _uploadError.value = true
            return
        }else if (!succeeded){
            // if just failed
            _uploadError.value = true
            return
        }
        _readyToNavToCharacters.value = true
    }

    fun submitCharacterDelete(){
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(tag, "starting to delete character")
            charId?.let { db.deleteCharacter(storyId, it, currentNames!!.filter { name -> name != originalCharacterName }) }
            character.value.imageFilename.value?.let { imageDB.deleteImage(it) }
        }
        _readyToNavToCharacters.value = true
    }

    fun alliesUpdated(charName: String, selected: Boolean){
        if (selected){
            //if selected add
            alliesList.add(charName)
        }else{
            // if unselected then remove
            alliesList.remove(charName)
        }
    }

    fun enemiesUpdated(charName: String, selected: Boolean){
        if (selected){
            //if selected add
            enemiesList.add(charName)
        }else{
            // if unselected then remove
            enemiesList.remove(charName)
        }
    }

    fun neutralsUpdated(charName: String, selected: Boolean){
        if (selected){
            //if selected add
            neutralList.add(charName)
        }else{
            // if unselected then remove
            neutralList.remove(charName)
        }
    }
}

/* time ensures that filenames are unique:
            can't go back in time even if you add character w/ image,
                change that story's character, (image keeps old name),
                and then try to add a character with the original character name
                    (which would have created two images with the same name)*/
fun getCharacterFilename(name: String, title: String) = "char_${name}_story_${title}_${Calendar.getInstance().time}"

class AddEditCharacterViewModelFactory(private val storyId: String, private val storyTitle: String, private val charName: String?, private val imageDB: ImageDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddEditCharacterViewModel(storyId, storyTitle, charName, imageDB) as T
}