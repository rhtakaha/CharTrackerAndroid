package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.R
import com.chartracker.database.MockUserDB
import com.chartracker.viewmodels.auth.SignUpViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignUpViewModelTest {
    private val mockUserDB = MockUserDB()
    private lateinit var viewmodel: SignUpViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() = runTest {
        viewmodel = SignUpViewModel(mockUserDB)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signUpCollisionTest() = runTest {
        viewmodel.signUpUserWithEmailPassword("collide", "password")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.signUpErrorMessage.value == R.string.user_collision_message)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signUpWeakPasswordTest() = runTest {
        viewmodel.signUpUserWithEmailPassword("weak", "password")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.signUpErrorMessage.value == "weak password")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signUpInvalidTest() = runTest {
        viewmodel.signUpUserWithEmailPassword("invalid", "password")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.signUpErrorMessage.value == R.string.malformed_email_message)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signUpOtherTest() = runTest {
        viewmodel.signUpUserWithEmailPassword("other", "password")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.signUpErrorMessage.value == R.string.unexpected_exception)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signUpTest() = runTest {
        viewmodel.signUpUserWithEmailPassword("email", "password")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.signedIn.value)
    }
}