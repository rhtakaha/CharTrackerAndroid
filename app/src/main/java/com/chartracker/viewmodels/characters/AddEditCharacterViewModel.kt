package com.chartracker.viewmodels.characters

import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import com.chartracker.database.ImageDBInterface
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import java.util.Date

class AddEditCharacterViewModel(
    private val storyId: String,
    private val storyTitle: String,
    charName: String?,
    private val imageDB: ImageDBInterface,
    private val characterDB: CharacterDBInterface
    ) : ViewModel() {

    private val tag = "AddEditCharVM"
    private var originalFilename: String? = null
    private lateinit var originalCharacterName: String
    private var charId: String? = null
    private var currentNames: MutableList<String> = mutableListOf()
    var currentFactions: MutableMap<String, Long> = mutableMapOf()

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    private var _charactersStringList: MutableList<String> = mutableListOf()
    val charactersStringList: List<String>
        get() = _charactersStringList

    @VisibleForTesting
    val alliesList = mutableListOf<String>()

    @VisibleForTesting
    val enemiesList = mutableListOf<String>()

    @VisibleForTesting
    val neutralList = mutableListOf<String>()

    private val factionsList = mutableListOf<String>()

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
            characterDB.getCurrentNames(storyId, currentNames, _retrievalError)
            characterDB.getCurrentFactions(storyId, currentFactions, _retrievalError)
            if (currentNames.isEmpty()){
                return@launch
            }
            if (charName != null){
                getCharacter(charName)
            }else{
                // when adding still need the list but don't need to remove themselves
                _charactersStringList = currentNames.toMutableList()
            }
        }
    }

    private suspend fun getCharacter(charName: String){
        charId = characterDB.getCharacterId(storyId, charName)
        if (charId != ""){
            characterDB.getCharacter(storyId, charName, _character, _retrievalError)
            if (_character.value.name.value != ""){
                originalCharacterName = character.value.name.value
                originalFilename = character.value.imageFilename.value
                _charactersStringList = currentNames.filter { name -> name != charName }.toMutableList()
                _character.value.allies.value?.let { alliesList.addAll(it) }
                _character.value.enemies.value?.let { enemiesList.addAll(it) }
                _character.value.neutral.value?.let { neutralList.addAll(it) }
                _character.value.faction.value?.let { factionsList.addAll(it) }
            }
        }else{
            _retrievalError.value = true
        }


    }

    /*function that calls a database access method to create the character in Firebase
        also calls navigation

        sanitizing the allies/enemies/neutrals/faction lists
        (in case a one of the ones contained in this character was deleted)
        */
    fun submitCharacter(newCharacter: CharacterEntity, localImageURI: Uri?){
        newCharacter.accessDate.value = Timestamp(Date())
        if (alliesList.size > 0){
            newCharacter.allies.value = alliesList.subtract(alliesList.subtract(currentNames.toSet())).toList()
        }
        if(enemiesList.size > 0){

            newCharacter.enemies.value = enemiesList.subtract(enemiesList.subtract(currentNames.toSet())).toList()
        }
        if (neutralList.size > 0){
            newCharacter.neutral.value = neutralList.subtract(neutralList.subtract(currentNames.toSet())).toList()
        }
        if (factionsList.size > 0){
            newCharacter.faction.value = factionsList.subtract(factionsList.subtract(currentFactions.keys)).toList()
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
        Timber.tag(tag).i("Creation of new char initiated")
        if (newCharacter.name.value in currentNames){
            _duplicateNameError.value = true
            return
        }

        // add the new name to the list
        currentNames.add(newCharacter.name.value)

        if (localImageURI != null){
            // adding an image
            newCharacter.imageFilename.value = getCharacterFilename(newCharacter.name.value, storyTitle)
            try {
                imageDB.addImage(newCharacter, localImageURI, _uploadError)
            }catch (exception: Exception){
                return
            }

        }
        characterDB.createCharacter(
            storyId,
            newCharacter,
            currentNames,
            readyToNavToCharacters = _readyToNavToCharacters,
            uploadError = _uploadError,
            localImageURI != null,
            deleteImage = {imageDB.deleteImage(newCharacter.imageFilename.value!!)}
        )
    }

    private suspend fun updateCharacter(updatedCharacter: CharacterEntity, localImageURI: Uri?, charId: String){
        Timber.tag("EditCharVM").i("starting to update character")
        if (updatedCharacter.name.value != originalCharacterName){
            // if the name was changed have to make sure it is valid
            if (updatedCharacter.name.value in currentNames){
                _duplicateNameError.value = true
                return
            }

            // add the new name to the list
            currentNames.add(updatedCharacter.name.value)
            // remove the name which is being replaced
            currentNames.remove(originalCharacterName)
        }

        if (localImageURI != null){
            if (!localImageURI.toString().startsWith("https://firebasestorage.googleapis.com")){
                // if this was NOT the original image
                // trying to add a new image
                updatedCharacter.imageFilename.value = getCharacterFilename(updatedCharacter.name.value, storyTitle)

                try {
                    imageDB.addImage(updatedCharacter, localImageURI, _uploadError, originalFilename)
                }catch (exception: Exception){
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

        characterDB.updateCharacter(
            storyId,
            charId,
            updatedCharacter,
            currentNames,
            originalCharacterName != updatedCharacter.name.value,
            localImageURI != null,
            readyToNavToCharacters = _readyToNavToCharacters,
            uploadError = _uploadError,
            deleteImage = {imageDB.deleteImage(updatedCharacter.imageFilename.value!!)}
            )
    }

    fun submitCharacterDelete(){
        CoroutineScope(Dispatchers.IO).launch {
            Timber.tag(tag).i("starting to delete character")
            charId?.let { characterDB.deleteCharacter(storyId, it, currentNames.filter { name -> name != originalCharacterName }) }
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

    fun factionsUpdated(factionName: String, selected: Boolean){
        if (selected){
            //if selected add
            factionsList.add(factionName)
        }else{
            // if unselected then remove
            factionsList.remove(factionName)
        }
    }
}

/* time ensures that filenames are unique:
            can't go back in time even if you add character w/ image,
                change that story's character, (image keeps old name),
                and then try to add a character with the original character name
                    (which would have created two images with the same name)*/
fun getCharacterFilename(name: String, title: String) = "char_${name}_story_${title}_${Calendar.getInstance().time}"

class AddEditCharacterViewModelFactory(
    private val storyId: String,
    private val storyTitle: String,
    private val charName: String?,
    private val imageDB: ImageDBInterface,
    private val characterDB: CharacterDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddEditCharacterViewModel(storyId, storyTitle, charName, imageDB, characterDB) as T
}