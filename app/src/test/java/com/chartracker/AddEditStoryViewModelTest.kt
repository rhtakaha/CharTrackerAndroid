package com.chartracker

import com.chartracker.database.MockImageDB
import com.chartracker.database.MockStoryDB
import com.chartracker.viewmodels.story.AddEditStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddEditStoryViewModelTest {
    private val mockStoryDB = MockStoryDB()
    private val mockImageDB = MockImageDB()
    private lateinit var viewmodel: AddEditStoryViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setupViewModel()= runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_initTest() = runTest {
        assert(!viewmodel.uploadError.value)
    }

}