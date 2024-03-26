package com.chartracker

import com.chartracker.database.MockStoryDB
import com.chartracker.viewmodels.story.AddEditStoryViewModel
import org.junit.Test

class AddEditStoryViewModelTest {
    private val mockStoryDB = MockStoryDB()
    private val viewmodel = AddEditStoryViewModel("id", mockStoryDB)

    @Test
    fun addEditStoryViewModel_initTest(){
        assert(!viewmodel.uploadError.value)
    }
}