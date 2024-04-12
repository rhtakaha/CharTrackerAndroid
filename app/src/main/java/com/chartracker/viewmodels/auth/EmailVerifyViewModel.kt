package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.UserDBInterface
import kotlinx.coroutines.launch

class EmailVerifyViewModel(private val userDB: UserDBInterface): ViewModel(){

    init {
        // sends the initial email when screen is opened
        sendVerificationEmail()
    }

    /* event for if user reload fails*/
    private val _failedReload = mutableStateOf(false)
    val failedReload: MutableState<Boolean>
        get() = _failedReload

    fun resetFailedReload(){
        _failedReload.value = false
    }

    /* event for email successfully sent to trigger snackbar*/
    private val _emailSent = mutableStateOf(false)
    val emailSent: MutableState<Boolean>
        get() = _emailSent

    fun resetEmailSent(){
        _emailSent.value = false
    }

    /* once on entry and then when the button it pressed*/
    fun sendVerificationEmail(){
        viewModelScope.launch {
            _emailSent.value = userDB.sendVerificationEmail()
        }
    }

    /*refreshes the current user and checks if the email is verified*/
    fun isEmailVerified(): Boolean{
        viewModelScope.launch {
            _failedReload.value = !userDB.isEmailVerified()
        }
        return !_failedReload.value
    }
}

class EmailVerifyViewModelFactory(private val userDB: UserDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EmailVerifyViewModel(userDB) as T
}