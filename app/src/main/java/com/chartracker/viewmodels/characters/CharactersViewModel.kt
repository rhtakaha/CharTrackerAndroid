package com.chartracker.viewmodels.characters

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



    init {
        viewModelScope.launch {
            getStoryId()
            getStory()
            getCharacters()
        }
    }

    private suspend fun getCharacters(){
        _characters.value = db.getCharacters(storyId)
    }

    private suspend fun getStoryId(){
        storyId = db.getStoryId(storyTitle)
    }

    private suspend fun getStory(){
        _story.value = db.getStoryFromId(storyId)
    }
}

class CharactersViewModelFactory(private val storyTitle: String) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CharactersViewModel(storyTitle) as T
}