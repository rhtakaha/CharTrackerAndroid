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

class CharactersViewModel(private val storyTitle: String, private val storyDB: StoryDBInterface, private val characterDB: CharacterDBInterface): ViewModel() {
    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters: StateFlow<List<CharacterEntity>> = _characters.asStateFlow()
    lateinit var storyId: String
    private val _story = mutableStateOf(StoryEntity())
    val story: MutableState<StoryEntity>
        get() = _story
//    private val _story = MutableStateFlow(StoryEntity())
//    val story: StateFlow<StoryEntity> = _story.asStateFlow()

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
                storyDB.getStoryFromId(storyId, _story)
                if (_story.value.name.value != ""){
                    getCharacters()
                }else{
                    _failedGetCharacters.value = true
                }
            }else{
                _failedGetCharacters.value = true
            }
        }
    }

    suspend fun getCharacters(){
        val temp = characterDB.getCharacters(storyId)
        if (temp != null){
            _characters.value = temp.sortedBy { character -> character.name.value }
        }else{
            _failedGetCharacters.value = true
        }

    }

    fun charactersAlphaSort(){
        _characters.value = _characters.value.sortedBy { character -> character.name.value }
    }

    fun charactersReverseAlphaSort(){
        _characters.value = _characters.value.sortedBy { character -> character.name.value }.reversed()
    }

    fun charactersRecentSort(){
        _characters.value = _characters.value.sortedBy { character -> character.accessDate.value }.reversed()
    }

    fun charactersReverseRecentSort(){
        _characters.value = _characters.value.sortedBy { character -> character.accessDate.value }
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