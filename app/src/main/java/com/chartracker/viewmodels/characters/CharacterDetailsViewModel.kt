package com.chartracker.viewmodels.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(private val storyId: String, private val charName: String): ViewModel() {
    private val db = DatabaseAccess()

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    var alliesList: String? = null
    var enemiesList: String? = null
    var neutralList: String? = null

    /* event for failing to get the character*/
    private val _failedGetCharacter = mutableStateOf(false)
    val failedGetCharacter: MutableState<Boolean>
        get() = _failedGetCharacter

    fun resetFailedGetCharacter(){
        _failedGetCharacter.value = false
    }

    init {
        setup()
    }

    fun setup(){
        viewModelScope.launch {
            getCharacter()
            if (_character.value.name.value != ""){
                alliesList = character.value.allies.value?.let { formatAssociatesList(it) }
                enemiesList = character.value.enemies.value?.let { formatAssociatesList(it) }
                neutralList = character.value.neutral.value?.let { formatAssociatesList(it) }
            }else{
                _failedGetCharacter.value = true
            }

        }
    }

    private suspend fun getCharacter() {
        _character.value =
            db.getCharacter(
                storyId = storyId,
                charName = charName
            )
    }

    private fun formatAssociatesList(associates: List<String>): String{
        var associatesString = ""
        for ((index, ally) in associates.withIndex()){
            associatesString = if (index == associates.lastIndex){
                associatesString.plus(ally)
            }else{
                associatesString.plus("$ally, ")
            }
        }
        return associatesString
    }
}

class CharacterDetailsViewModelFactory(private val storyId: String, private val charName: String) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CharacterDetailsViewModel(storyId, charName) as T
}