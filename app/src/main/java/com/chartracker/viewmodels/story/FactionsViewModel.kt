package com.chartracker.viewmodels.story

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterDBInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FactionsViewModel(private val storyId: String, private val characterDB: CharacterDBInterface): ViewModel() {

    private val defaultColor = 0xFF0000FF

    /* event for failing to get factions*/
    private val _failedGetFactions = mutableStateOf(false)
    val failedGetFactions: MutableState<Boolean>
        get() = _failedGetFactions

    fun resetFailedGetFactions(){
        _failedGetFactions.value = false
    }

    /* event for non-unique faction name*/
    private val _duplicateNameError = mutableStateOf(false)
    val duplicateNameError: MutableState<Boolean>
        get() = _duplicateNameError

    fun resetDuplicateNameError(){
        _duplicateNameError.value = false
    }

    /* event for failing to updated factions*/
    private val _failedUpdateFactions = mutableStateOf(false)
    val failedUpdateFactions: MutableState<Boolean>
        get() = _failedUpdateFactions

    fun resetFailedUpdateFactions(){
        _failedUpdateFactions.value = false
    }

    private val _factions = MutableStateFlow<MutableMap<String, Long>>(mutableMapOf())
    val factions: StateFlow<Map<String, Long>> = _factions.asStateFlow()

    init {
        viewModelScope.launch {
            getFactions()
            if (_factions.value.isEmpty()){
                return@launch
            }
        }
    }

    suspend fun getFactions(){
        _factions.value.clear()
        characterDB.getCurrentFactions(storyId, _factions.value, _failedGetFactions)
    }

    /* adds a faction to the map in the viewmodel (with default color)*/
    fun createFaction(originalName: String){
        if (originalName !in _factions.value.keys) {
            _factions.value[originalName] = defaultColor
        }else{
            _duplicateNameError.value = true
        }
    }

    /* updates the specific faction within the map in the viewmodel*/
    fun updateFaction(originalName: String, currentName: String, color: Long){
        if (originalName in _factions.value.keys){
            if (originalName != currentName){
                // if changing the name of the faction
                _factions.value.remove(originalName)
                _factions.value[currentName] = color
            }else{
                // keeping the same name
                _factions.value[originalName] = color
            }
        }
    }

    /* removes a faction from within the map in the viewmodel*/
    fun deleteFaction(originalName: String){
        _factions.value.remove(originalName)
    }

    /* submits all the changes to factions (including additions and removals) to the database*/
    fun submitFactions(){
        viewModelScope.launch {
            characterDB.updateFactions(storyId, _factions.value, _failedUpdateFactions)
        }
    }


}

class FactionsViewModelFactory(
    private val storyId: String,
    private val characterDB: CharacterDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        FactionsViewModel(storyId, characterDB) as T
}