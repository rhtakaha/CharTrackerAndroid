package com.chartracker.viewModelTests

import android.net.Uri
import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockImageDB
import com.chartracker.database.MockStoryDB
import com.chartracker.database.StoryEntity
import com.chartracker.viewmodels.story.AddEditStoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddEditStoryViewModelTest {
    private val mockStoryDB = MockStoryDB()
    private val mockImageDB = MockImageDB()
    private lateinit var viewmodel: AddEditStoryViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_initTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        assert(!viewmodel.uploadError.value)
    }

    /***************************************UPDATE*************************************************/
    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStory_updateTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStory_updateFailTest() = runTest {
        viewmodel = AddEditStoryViewModel("incorrect", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStory_titleTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.duplicateTitleError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStory_duplicateTitleTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("Dune"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.duplicateTitleError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStory_imageUploadTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), Uri.parse("fileUri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_updateStory_imageUploadErrorTest() = runTest {
        viewmodel = AddEditStoryViewModel("id", mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title", imageFilename = "incorrect"), Uri.parse("file uri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    /****************************************ADD***************************************************/
    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_addStory_createStoryTest() = runTest {
        viewmodel = AddEditStoryViewModel(null, mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_addStory_createStoryFailTest() = runTest {
        viewmodel = AddEditStoryViewModel(null, mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("not title"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_addStory_titleTest() = runTest {
        viewmodel = AddEditStoryViewModel(null, mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.duplicateTitleError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_addStory_duplicateTitleTest() = runTest {
        viewmodel = AddEditStoryViewModel(null, mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("Dune"), null)

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.duplicateTitleError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_addStory_imageUploadTest() = runTest {
        viewmodel = AddEditStoryViewModel(null, mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title"), Uri.parse("fileUri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.uploadError.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun addEditStoryViewModel_addStory_imageUploadErrorTest() = runTest {
        viewmodel = AddEditStoryViewModel(null, mockStoryDB, mockImageDB)
        viewmodel.submitStory(StoryEntity("title", imageFilename = "incorrect"), Uri.parse("file uri"))

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.uploadError.value)
    }

}