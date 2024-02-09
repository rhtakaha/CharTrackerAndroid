package com.chartracker.viewmodels.characters

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.CharacterEntity
import com.chartracker.database.DatabaseAccess
import kotlinx.coroutines.launch

class CharacterDetailsViewModel(private val storyId: String, private val charName: String): ViewModel() {
    private val db = DatabaseAccess()

    private val _character = mutableStateOf(CharacterEntity())
    val character: MutableState<CharacterEntity>
        get() = _character

    init {
        viewModelScope.launch {
            getCharacter()
        }
    }

    private suspend fun getCharacter() {
        _character.value =
            db.getCharacter(
                storyId = storyId,
                charName = charName
            )
    }
}

class CharacterDetailsViewModelFactory(private val storyId: String, private val charName: String) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CharacterDetailsViewModel(storyId, charName) as T
}