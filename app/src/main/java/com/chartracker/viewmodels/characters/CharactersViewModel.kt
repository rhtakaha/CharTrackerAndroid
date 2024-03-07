package com.chartracker.viewmodels.characters

import android.util.Log
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
                    if (_characters.value.isEmpty()){
                        Log.d("chars", "at characters")
                        _failedGetCharacters.value = true
                    }
                }else{
                    Log.d("chars", "at story")
                    _failedGetCharacters.value = true
                }
            }else{
                Log.d("chars", "at id")
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
            _characters.value = db.getCharacters(storyId)

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