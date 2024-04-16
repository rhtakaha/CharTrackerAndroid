package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockUserDB
import com.chartracker.viewmodels.auth.SignInViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SignInViewModelTest {
    private val mockUserDB = MockUserDB()
    private lateinit var viewmodel: SignInViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() = runTest {
        viewmodel = SignInViewModel(mockUserDB)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun initTest() = runTest {
        assert(!viewmodel.signedIn.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun sendPasswordResetEmailFailTest() = runTest {
        viewmodel.sendPasswordResetEmail("FAIL")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.emailSent.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun sendPasswordResetEmailTest() = runTest {
        viewmodel.sendPasswordResetEmail("email")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.emailSent.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signInWithEmailPasswordFailTest() = runTest {
        viewmodel.signInWithEmailPassword("email", "FAIL")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.invalidCredentials.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signInWithEmailPasswordUnverifiedTest() = runTest {
        viewmodel.signInWithEmailPassword("email", "unverified")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.unverifiedEmail.value)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signInWithEmailPasswordTest() = runTest {
        viewmodel.signInWithEmailPassword("email", "correct")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.signedIn.value)
    }

}