package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockCharacterDB
import com.chartracker.viewmodels.story.FactionsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FactionsViewModelTest {
    private val mockCharacterDB = MockCharacterDB()
    private lateinit var viewmodel: FactionsViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    @Test
    fun init_failedGetFactionsTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "xd",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.failedGetFactions.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun init_getFactionsTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "id",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.failedGetFactions.value)
        assert(viewmodel.factions.value.containsKey("Straw Hat Pirates"))
        assert(viewmodel.factions.value.containsKey("Silver Fox Pirates"))
        assert(viewmodel.factions.value.containsKey("World Government"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addFactionTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "id",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        viewmodel.createFaction("Heart Pirates")

        assert(viewmodel.factions.value.containsKey("Heart Pirates"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateFactionTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "id",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        viewmodel.updateFaction("Straw Hat Pirates", "Straw Hats", 0xffa4ffa3)

        assert(!viewmodel.factions.value.containsKey("Straw Hat Pirates"))
        assert(viewmodel.factions.value.containsKey("Straw Hats"))
        assert(viewmodel.factions.value.containsValue(0xffa4ffa3))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteFactionTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "id",
            characterDB = mockCharacterDB
        )

        //allows for the internal coroutines to run
        advanceUntilIdle()

        viewmodel.deleteFaction("Straw Hat Pirates")

        assert(!viewmodel.factions.value.containsKey("Straw Hat Pirates"))
    }

    @ExperimentalCoroutinesApi
    @Test
    fun submitFactionTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "id",
            characterDB = mockCharacterDB
        )
        viewmodel.submitFactions()

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.failedUpdateFactions.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun submitFactionFailTest() = runTest {
        viewmodel = FactionsViewModel(
            storyId = "xd",
            characterDB = mockCharacterDB
        )
        viewmodel.submitFactions()

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.failedUpdateFactions.value)
    }
}