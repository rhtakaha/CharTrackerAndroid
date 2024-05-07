package com.chartracker.viewmodels.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterDBInterface
import com.chartracker.database.CharacterEntity
import com.chartracker.database.StoryDBInterface
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class CharactersViewModel(private val storyTitle: String, private val storyDB: StoryDBInterface, private val characterDB: CharacterDBInterface): ViewModel() {
    private val _characters = MutableStateFlow<MutableList<CharacterEntity>>(mutableListOf())
    val characters: StateFlow<List<CharacterEntity>> = _characters.asStateFlow()
    lateinit var storyId: String
    private val _story = mutableStateOf(StoryEntity())
    val story: MutableState<StoryEntity>
        get() = _story

    /* event for failing to get characters*/
    private val _failedGetCharacters = mutableStateOf(false)
    val failedGetCharacters: MutableState<Boolean>
        get() = _failedGetCharacters

    fun resetFailedGetCharacters(){
        _failedGetCharacters.value = false
    }


    init {
        viewModelScope.launch {
            storyId = storyDB.getStoryId(storyTitle)
            if (storyId != ""){
                storyDB.getStoryFromId(storyId, _story, _failedGetCharacters)
                getCharacters()
            }else{
                Timber.tag("CharactersVM").i("failed get getStoryId")
                _failedGetCharacters.value = true
            }
        }
    }

    suspend fun getCharacters(){
        _characters.value.clear()
        characterDB.getCharacters(storyId, _characters, _failedGetCharacters)
    }

    fun charactersAlphaSort(){
        _characters.value = _characters.value.sortedBy { character -> character.name.value }.toMutableList()
    }

    fun charactersReverseAlphaSort(){
        _characters.value =
            _characters.value.sortedBy { character -> character.name.value }.reversed().toMutableList()
    }

    fun charactersRecentSort(){
        _characters.value =
            _characters.value.sortedBy { character -> character.accessDate.value }.reversed().toMutableList()
    }

    fun charactersReverseRecentSort(){
        _characters.value = _characters.value.sortedBy { character -> character.accessDate.value }.toMutableList()
    }
}

class CharactersViewModelFactory(
    private val storyTitle: String,
    private val storyDB: StoryDBInterface,
    private val characterDB: CharacterDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CharactersViewModel(storyTitle, storyDB, characterDB) as T
}