package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockCharacterDB
import com.chartracker.database.MockImageDB
import com.chartracker.viewmodels.characters.AddEditCharacterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddEditCharacterViewModelTest {
    private val mockCharacterDB = MockCharacterDB()
    private val mockImageDB = MockImageDB()
    private lateinit var viewmodel: AddEditCharacterViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**********************************   ADD   ***************************************************/
    @ExperimentalCoroutinesApi
    @Test
    fun init_getCurrentNamesTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "id",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.retrievalError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getCurrentNamesFailTest() = runTest {
        viewmodel = AddEditCharacterViewModel(
            storyId = "",
            storyTitle = "title",
            charName = null,
            imageDB = mockImageDB,
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.retrievalError.value)
    }
}