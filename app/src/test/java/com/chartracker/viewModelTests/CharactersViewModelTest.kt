package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockCharacterDB
import com.chartracker.database.MockStoryDB
import com.chartracker.viewmodels.characters.CharactersViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CharactersViewModelTest {
    private val mockCharacterDB = MockCharacterDB()
    private val mockStoryDB = MockStoryDB()
    private lateinit var viewmodel: CharactersViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    @Test
    fun init_getStoryIdFailTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.failedGetCharacters.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getStoryIdTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "Lord of the Rings", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.storyId == "id")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getStoryFromIdTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "Lord of the Rings", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.story.value.name.value == "Lord of the Rings")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getStoryFromIdFailTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "xd", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.storyId == "not")
        assert(viewmodel.failedGetCharacters.value)
    }


    @ExperimentalCoroutinesApi
    @Test
    fun init_getCharactersTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "Lord of the Rings", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.characters.value[0].name.value == "Aragorn")
        assert(viewmodel.characters.value[1].name.value == "Frodo")
        assert(viewmodel.characters.value[2].name.value == "Sam")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getCharactersFailTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "Dune", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.failedGetCharacters.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getCharacters_alphaSortTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "Lord of the Rings", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        viewmodel.charactersAlphaSort()

        assert(viewmodel.characters.value[0].name.value == "Aragorn")
        assert(viewmodel.characters.value[1].name.value == "Frodo")
        assert(viewmodel.characters.value[2].name.value == "Sam")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getCharacters_reverseAlphaSortTest() = runTest {
        viewmodel = CharactersViewModel(storyTitle = "Lord of the Rings", storyDB = mockStoryDB, characterDB = mockCharacterDB)

        //allows for the internal coroutines to run
        advanceUntilIdle()
        viewmodel.charactersReverseAlphaSort()

        assert(viewmodel.characters.value[2].name.value == "Aragorn")
        assert(viewmodel.characters.value[1].name.value == "Frodo")
        assert(viewmodel.characters.value[0].name.value == "Sam")
    }

}