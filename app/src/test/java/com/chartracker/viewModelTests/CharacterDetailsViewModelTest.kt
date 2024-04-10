package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockCharacterDB
import com.chartracker.viewmodels.characters.CharacterDetailsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CharacterDetailsViewModelTest {
    private val mockCharacterDB = MockCharacterDB()
    private lateinit var viewmodel: CharacterDetailsViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    @Test
    fun init_emptyCharacterTest() = runTest {
        viewmodel = CharacterDetailsViewModel(
            storyId = "id",
            charName = "Sauron",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.failedGetCharacter.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateAlliesTest() = runTest {
        viewmodel = CharacterDetailsViewModel(
            storyId = "id",
            charName = "Frodo",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.character.value.allies.value == listOf("Sam", "Aragorn"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateEnemiesTest() = runTest {
        viewmodel = CharacterDetailsViewModel(
            storyId = "id",
            charName = "Frodo",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()
        println(viewmodel.character.value.enemies.value)

        assert(viewmodel.character.value.enemies.value == listOf("Sauruman", "Witch King"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateNeutralsTest() = runTest {
        viewmodel = CharacterDetailsViewModel(
            storyId = "id",
            charName = "Frodo",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.character.value.neutral.value == listOf<String>())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun alliesFormattingTest() = runTest {
        viewmodel = CharacterDetailsViewModel(
            storyId = "id",
            charName = "Frodo",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.alliesList == "Sam, Aragorn")
    }
}