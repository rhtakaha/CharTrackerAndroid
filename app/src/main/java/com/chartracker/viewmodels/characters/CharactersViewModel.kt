package com.chartracker.viewmodels.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.StoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharactersViewModel(private val storyTitle: String): ViewModel() {
    private val _characters = MutableStateFlow<List<CharacterEntity>>(emptyList())
    val characters: StateFlow<List<CharacterEntity>> = _characters.asStateFlow()
    private val db = DatabaseAccess()
    lateinit var storyId: String
    private val _story = MutableStateFlow(StoryEntity())
    val story: StateFlow<StoryEntity> = _story.asStateFlow()

    /* event for failing to get characters*/
    private val _failedGetCharacters = mutableStateOf(false)
    val failedGetCharacters: MutableState<Boolean>
        get() = _failedGetCharacters

    fun resetFailedGetCharacters(){
        _failedGetCharacters.value = false
    }


    init {
        viewModelScope.launch {
            getStoryId()
            if (storyId != ""){
                getStory()
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

    private suspend fun getStoryId(){
        storyId = db.getStoryId(storyTitle)
    }

    private suspend fun getStory(){
        _story.value = db.getStoryFromId(storyId)
    }

    suspend fun getCharacters(){
        val temp = db.getCharacters(storyId)
        if (temp != null){
            _characters.value = temp
        }else{
            _failedGetCharacters.value = true
        }

    }

//    fun getCharacters(){
//        viewModelScope.launch {
//            _characters.value = db.getCharacters(storyId)
//        }
//    }
}

class CharactersViewModelFactory(private val storyTitle: String) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CharactersViewModel(storyTitle) as T
}