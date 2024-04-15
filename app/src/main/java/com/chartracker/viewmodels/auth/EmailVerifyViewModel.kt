package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.UserDBInterface
import kotlinx.coroutines.launch

class EmailVerifyViewModel(private val userDB: UserDBInterface): ViewModel(){

    private val _updatedEmail = mutableStateOf("")
    val updatedEmail: MutableState<String>
        get() = _updatedEmail

    fun updateUpdatedEmail(new: String){
        _updatedEmail.value = new
    }

    /*event for invalid user*/
    private val _invalidUser = mutableStateOf(false)
    val invalidUser: MutableState<Boolean>
        get() = _invalidUser

    fun resetInvalidUser(){
        _invalidUser.value = false
    }

    /*event for email updated (so verification email on the way)*/
    private val _updateEmailVerificationSent = mutableStateOf(false)
    val updateEmailVerificationSent: MutableState<Boolean>
        get() = _updateEmailVerificationSent

    fun resetUpdateEmailVerificationSent(){
        _updateEmailVerificationSent.value = false
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

    init {
        // sends the initial email when screen is opened
        sendVerificationEmail()
    }

    /*refreshes the current user and checks if the email is verified*/
    fun isEmailVerified(): Boolean{
        viewModelScope.launch {
            _failedReload.value = !userDB.isEmailVerified()
        }
        return !_failedReload.value
    }

    //sends the user a verification email to the new email which completes the change
    fun updateUserEmail(newEmail: String){
        viewModelScope.launch {
            if (userDB.updateUserEmail(newEmail)){
                _updateEmailVerificationSent.value = true
            }else{
                _invalidUser.value = true
            }
        }

    }
}

class EmailVerifyViewModelFactory(private val userDB: UserDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        EmailVerifyViewModel(userDB) as T
}