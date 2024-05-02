package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockUserDB
import com.chartracker.viewmodels.auth.EmailVerifyViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmailVerifyViewModelTest {
    private val mockUserDB = MockUserDB()
    private lateinit var viewmodel: EmailVerifyViewModel

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() = runTest {
        viewmodel = EmailVerifyViewModel(mockUserDB)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun initTest() = runTest {
        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.emailSent.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun verificationTest() = runTest {
        val output = viewmodel.isEmailVerified()
        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(output)
        assert(viewmodel.verified.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateUserEmailFailTest() = runTest {
        viewmodel.updateUserEmail("FAIL")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.invalidUser.value)
        viewmodel.resetInvalidUser()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updateUserEmailTest() = runTest {
        viewmodel.updateUserEmail("new")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.updateEmailVerificationSent.value)
        viewmodel.resetUpdateEmailVerificationSent()
    }
}