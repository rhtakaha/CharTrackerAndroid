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

class AddEditCharacterViewModel(private val storyId: String, private val storyTitle: String, charName: String?): ViewModel() {
    private val tag = "AddEditCharVM"
    private val db = DatabaseAccess()
    private var originalFilename: String? = null
    private var charId: String? = null

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    private fun updateLocalCharacter(new: CharacterEntity){
        _character.value = new
    }

    //probably just here for passing along later? check on that
//    private val _charactersStringList = MutableStateFlow<MutableList<String>>(mutableListOf())
//    val charactersStringList: StateFlow<MutableList<String>> = _charactersStringList.asStateFlow()
    private var _charactersStringList: MutableList<String> = mutableListOf()
    val charactersStringList: List<String>
        get() = _charactersStringList

    /* function to update the list of all the character names (as Strings)
        which we will pass to edit/add Character*/
    private suspend fun updateCharsStringList(){
        val characters = db.getCharacters(storyId)
        _charactersStringList.clear()
        for (character in characters){
            character.name.value.let { _charactersStringList.add(it) }
        }
    }

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

    init {
        viewModelScope.launch {
            updateCharsStringList()
            if (charName != null){
                getCharacter(charName)
            }
        }
    }

    private suspend fun getCharacter(charName: String){
        charId = db.getCharacterId(storyId, charName)
        updateLocalCharacter(db.getCharacter(storyId, charName))
        originalFilename = character.value.imageFilename.value
        _charactersStringList = _charactersStringList.filter { name -> name != charName }.toMutableList()

    }

    /*function that calls a database access method to create the character in Firebase
        also calls navigation*/
    fun submitCharacter(newCharacter: CharacterEntity, localImageURI: Uri?){
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
            if (!localImageURI.toString().startsWith("https://firebasestorage.googleapis.com")){
                // if this was NOT the original image
                // trying to add a new image
                //if adding a new image be sure to delete the original too (if it had one)
                updatedCharacter.imageFilename.value = getCharacterFilename(updatedCharacter.name.value, storyTitle)
                db.addImage(updatedCharacter.imageFilename.value!!, localImageURI)
                db.addImageDownloadUrlToCharacter(updatedCharacter, updatedCharacter.imageFilename.value!!)

                //if adding a new image be sure to delete the original too (if it had one)
                originalFilename?.let { it1 -> db.deleteImage(it1) }
            }
            // if this was just the existing image don't do anything

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

class AddEditCharacterViewModelFactory(private val storyId: String, private val storyTitle: String, private val charName: String?) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AddEditCharacterViewModel(storyId, storyTitle, charName) as T
}