package com.chartracker.viewmodels.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import kotlinx.coroutines.launch
import timber.log.Timber

class CharacterDetailsViewModel(private val storyId: String, private val charName: String, private val characterDB: CharacterDBInterface): ViewModel() {

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    var alliesList: String? = null
    var enemiesList: String? = null
    var neutralList: String? = null
    var factionsList: String? = null
    private var currentNames: MutableList<String> = mutableListOf()
    private var currentFactions: MutableMap<String, Long> = mutableMapOf()

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
            verifyAssociatesLists()
        }
    }

    private suspend fun getCharacter(){
        viewModelScope.launch {
            characterDB.getCharacter(storyId,charName,_character, _failedGetCharacter)

            characterDB.getCurrentNames(storyId, currentNames, _failedGetCharacter)
            characterDB.getCurrentFactions(storyId, currentFactions, _failedGetCharacter)
        }.join()
        Timber.tag("Details").i("finished getting character: ${_character.value.name.value} and found current factions: $currentFactions")
    }

    private fun verifyAssociatesLists(){
        /* rely on a "we'll get them next time" approach since
        as long as the user facing info is correct,
        the db can be wrong for a bit longer until they come back to this page
        should not be able to really effect performance*/
        Timber.tag("Details").i("verifying the associates of character: ${_character.value.name.value}")
        if (currentNames.isEmpty()){
            // if failed to get them then do not change anything
            return
        }

        /* performing the set operation: associates - (associates - currentNames)*/
        val allies = character.value.allies.value
        if (allies != null){
            character.value.allies.value = allies.subtract(allies.subtract(currentNames.toSet())).toList()
        }

        val enemies = character.value.enemies.value
        if (enemies != null){
            character.value.enemies.value = enemies.subtract(enemies.subtract(currentNames.toSet())).toList()
        }

        val neutrals = character.value.neutral.value
        if (neutrals != null){
            character.value.neutral.value = neutrals.subtract(neutrals.subtract(currentNames.toSet())).toList()
        }

        val factions = character.value.faction.value
        if (factions != null){
            character.value.faction.value = factions.subtract(factions.subtract(currentFactions.keys)).toList()
        }

        alliesList = character.value.allies.value?.let { formatAssociatesList(it) }
        enemiesList = character.value.enemies.value?.let { formatAssociatesList(it) }
        neutralList = character.value.neutral.value?.let { formatAssociatesList(it) }
        factionsList = character.value.faction.value?.let { formatAssociatesList(it) }
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

class CharacterDetailsViewModelFactory(private val storyId: String, private val charName: String, private val characterDB: CharacterDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CharacterDetailsViewModel(storyId, charName, characterDB) as T
}