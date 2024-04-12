package com.chartracker.viewModelTests

import com.chartracker.MainDispatcherRule
import com.chartracker.database.MockUserDB
import com.chartracker.viewmodels.auth.SettingsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsViewModelTest {
    private val mockUserDB = MockUserDB()
    private lateinit var viewmodel: SettingsViewModel


    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() = runTest {
        viewmodel = SettingsViewModel(mockUserDB)
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

    @ExperimentalCoroutinesApi
    @Test
    fun updatePasswordInvalidFailTest() = runTest {
        viewmodel.updatePassword("invalid")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.invalidUser.value)
        viewmodel.resetInvalidUser()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updatePasswordWeakFailTest() = runTest {
        viewmodel.updatePassword("weak")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        println(viewmodel.weakPassword.value)

        assert(viewmodel.weakPassword.value == "weak password")
        viewmodel.resetWeakPassword()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updatePasswordReAuthFailTest() = runTest {
        viewmodel.updatePassword("reauth")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.triggerReAuth.value)
        viewmodel.resetTriggerReAuth()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun updatePasswordTest() = runTest {
        viewmodel.updatePassword("new")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.passwordUpdateSuccess.value)
        viewmodel.resetPasswordUpdateSuccess()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun signOutTest() = runTest {
        viewmodel.signOut()

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.readyToNavToSignIn.value)
        viewmodel.resetReadyToNavToSignIn()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserReAuthFailTest() = runTest {
        viewmodel.deleteUser("reauth")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.triggerReAuth.value)
        viewmodel.resetTriggerReAuth()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserInvalidFailTest() = runTest {
        viewmodel.deleteUser("invalid")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.invalidUser.value)
        viewmodel.resetInvalidUser()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun deleteUserTest() = runTest {
        viewmodel.deleteUser("gone")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.readyToNavToSignIn.value)
        viewmodel.resetReadyToNavToSignIn()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun reAuthUserFailTest() = runTest {
        viewmodel.reAuthUser("FAIL")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(viewmodel.invalidUser.value)
        viewmodel.resetInvalidUser()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun reAuthUserTest() = runTest {
        viewmodel.reAuthUser("password")

        //allows for the internal coroutines to run
        advanceUntilIdle()

        assert(!viewmodel.invalidUser.value)
        viewmodel.resetInvalidUser()
    }
}