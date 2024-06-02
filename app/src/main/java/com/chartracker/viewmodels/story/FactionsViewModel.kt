package com.chartracker.viewmodels.story

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterDBInterface
import kotlinx.coroutines.launch

class FactionsViewModel(private val storyId: String, private val characterDB: CharacterDBInterface): ViewModel() {

    /* event for failing to get factions*/
    private val _failedGetFactions = mutableStateOf(false)
    val failedGetFactions: MutableState<Boolean>
        get() = _failedGetFactions

    fun resetFailedGetFactions(){
        _failedGetFactions.value = false
    }

    val factions = mutableMapOf<String, Long>()

    init {
        viewModelScope.launch {
            getFactions()
            if (factions.isEmpty()){
                return@launch
            }
        }
    }

    suspend fun getFactions(){
        factions.clear()
        characterDB.getCurrentFactions(storyId, factions, _failedGetFactions)
    }

    fun updateFaction(originalName: String, currentName: String, color: Long){
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