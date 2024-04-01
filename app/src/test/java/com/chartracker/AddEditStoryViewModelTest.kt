package com.chartracker

import android.net.Uri
import com.chartracker.database.MockImageDB
import com.chartracker.database.MockStoryDB
import com.chartracker.database.StoryEntity
import com.chartracker.viewmodels.story.AddEditStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class AddEditStoryViewModelTest {
    private val mockStoryDB = MockStoryDB()
    private val mockImageDB = MockImageDB()
    private lateinit var viewmodel: AddEditStoryViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

//    @Before
//    fun setupViewModel()= runTest {
//        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
//    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_initTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStoryTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), Uri.EMPTY)
        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_getStoryNullTest() = runTest {
        viewmodel = AddEditStoryViewModel("incorrect", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), Uri.EMPTY)
        assert(!viewmodel.uploadError.value)
    }

}