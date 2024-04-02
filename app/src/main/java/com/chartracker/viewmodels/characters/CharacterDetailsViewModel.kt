package com.chartracker.viewmodels.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch
import timber.log.Timber

class CharacterDetailsViewModel(private val storyId: String, private val charName: String): ViewModel() {
    private val db = DatabaseAccess()

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    var alliesList: String? = null
    var enemiesList: String? = null
    var neutralList: String? = null
    private var currentNames: List<String>? = null

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
            if (getCharacter()){
                verifyAssociatesLists()
                alliesList = character.value.allies.value?.let { formatAssociatesList(it) }
                enemiesList = character.value.enemies.value?.let { formatAssociatesList(it) }
                neutralList = character.value.neutral.value?.let { formatAssociatesList(it) }
            }else{
                _failedGetCharacter.value = true
            }
        }
    }

    private suspend fun getCharacter(): Boolean{
        _character.value =
            db.getCharacter(
                storyId = storyId,
                charName = charName
            )

        currentNames = db.getCurrentNames(storyId)

        return !(currentNames == null || _character.value.name.value == "")
    }

    private suspend fun verifyAssociatesLists(){
        /* rely on a "we'll get them next time" approach since
        as long as the user facing info is correct,
        the db can be wrong for a bit longer until they come back to this page
        should not be able to really effect performance*/

        /* performing the set operation: associates - (associates - currentNames)*/
        val allies = character.value.allies.value
        var updatedAllies: List<String> = listOf()
        if (allies != null){
            updatedAllies = allies.subtract(allies.subtract(currentNames!!.toSet())).toList()
            character.value.allies.value = updatedAllies
            Timber.tag("details").d("allies changed")
        }

        val enemies = character.value.enemies.value
        var updatedEnemies: List<String> = listOf()
        if (enemies != null){
            updatedEnemies = enemies.subtract(enemies.subtract(currentNames!!.toSet())).toList()
            character.value.enemies.value = updatedEnemies
            Timber.tag("details").d("enemies changed")
        }

        val neutrals = character.value.neutral.value
        var updatedNeutrals: List<String> = listOf()
        if (neutrals != null){
            updatedNeutrals = neutrals.subtract(neutrals.subtract(currentNames!!.toSet())).toList()
            character.value.neutral.value = updatedNeutrals
            Timber.tag("details").d("neutrals changed")
        }


        if (allies != updatedAllies || enemies != updatedEnemies || neutrals != updatedNeutrals){
            // if there was a difference then update

            val charId = db.getCharacterId(storyId, charName)
            if (charId != ""){
                // again if it fails no need to report to the user
                db.updateCharacter(storyId, charId, character.value, null)
            }
        }
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