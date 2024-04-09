package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockStoryDB
import com.chartracker.viewmodels.story.StoriesViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StoriesViewModelTest {
    private val mockStoryDB = MockStoryDB()
    private lateinit var viewmodel: StoriesViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() = runTest {
        viewmodel = StoriesViewModel(mockStoryDB)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun initTest() = runTest {
        assert(viewmodel.stories.value[0].name.value == "Dune")
        assert(viewmodel.stories.value[1].name.value == "Lord of the Rings")
        assert(viewmodel.stories.value[2].name.value == "Star Wars")
    }

    @Test
    fun reverseAlphaSortTest(){
        viewmodel.storiesReverseAlphaSort()

        assert(viewmodel.stories.value[2].name.value == "Dune")
        assert(viewmodel.stories.value[1].name.value == "Lord of the Rings")
        assert(viewmodel.stories.value[0].name.value == "Star Wars")
    }

    @Test
    fun alphaSortTest(){
        viewmodel.storiesAlphaSort()

        assert(viewmodel.stories.value[0].name.value == "Dune")
        assert(viewmodel.stories.value[1].name.value == "Lord of the Rings")
        assert(viewmodel.stories.value[2].name.value == "Star Wars")
    }

    /**
     * Can't test the access date sorting because that is all internal to the story
     * with no external accessors.
     * Could expose it, but mostly needless and makes errors easier later on
     * **/
}