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

    /* event for failing to get factions*/
    private val _failedGetFactions = mutableStateOf(false)
    val failedGetFactions: MutableState<Boolean>
        get() = _failedGetFactions

    fun resetFailedGetFactions(){
        _failedGetFactions.value = false
    }

    private val _factions = MutableStateFlow<MutableMap<String, Long>>(mutableMapOf())
    val factions: StateFlow<Map<String, Long>> = _factions.asStateFlow()
    //TODO - might want/ need to change to flow or something
//    val factions = mutableMapOf<String, Long>()

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
        //TODO
    }

    /* updates the specific faction within the map in the viewmodel*/
    fun updateFaction(originalName: String, currentName: String, color: Long){
        //TODO
    }

    /* removes a faction from within the map in the viewmodel*/
    fun deleteFaction(originalName: String){
        //TODO
    }

    /* submits all the changes to factions (including additions and removals) to the database*/
    fun submitFactions(){
        //TODO
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