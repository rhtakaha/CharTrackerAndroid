package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.UserDBInterface
import kotlinx.coroutines.launch


class SettingsViewModel(private val userDB: UserDBInterface): ViewModel(){

    private val _updatedEmail = mutableStateOf("")
    val updatedEmail: MutableState<String>
        get() = _updatedEmail

    fun updateUpdatedEmail(new: String){
        _updatedEmail.value = new
    }

    private val _updatedPassword = mutableStateOf("")
    val updatedPassword: MutableState<String>
        get() = _updatedPassword

    fun updateUpdatedPassword(new: String){
        _updatedPassword.value = new
    }

    private val _confirmedPassword = mutableStateOf("")
    val confirmedPassword: MutableState<String>
        get() = _confirmedPassword

    fun updateConfirmedPassword(new: String){
        _confirmedPassword.value = new
    }

    /*navigate to sign in event (on sign out or account delete)*/
    private val _readyToNavToSignIn = mutableStateOf(false)
    val readyToNavToSignIn: MutableState<Boolean>
        get() = _readyToNavToSignIn

    fun resetReadyToNavToSignIn(){
        _readyToNavToSignIn.value = false
    }

    /*event for email updated (so verification email on the way)*/
    private val _updateEmailVerificationSent = mutableStateOf(false)
    val updateEmailVerificationSent: MutableState<Boolean>
        get() = _updateEmailVerificationSent

    fun resetUpdateEmailVerificationSent(){
        _updateEmailVerificationSent.value = false
    }

    /*event for weak new password*/
    private val _weakPassword = mutableStateOf("")
    val weakPassword: MutableState<String>
        get() = _weakPassword

    fun resetWeakPassword(){
        _weakPassword.value = ""
    }

    /*event for invalid user*/
    private val _invalidUser = mutableStateOf(false)
    val invalidUser: MutableState<Boolean>
        get() = _invalidUser

    fun resetInvalidUser(){
        _invalidUser.value = false
    }

    /*event for triggering reauthentication if user signed in too long ago*/
    private val _triggerReAuth = mutableStateOf(false)
    val triggerReAuth: MutableState<Boolean>
        get() = _triggerReAuth

    fun resetTriggerReAuth(){
        _triggerReAuth.value = false
    }

    /*event for successful password update*/
    private val _passwordUpdateSuccess = mutableStateOf(false)
    val passwordUpdateSuccess: MutableState<Boolean>
        get() = _passwordUpdateSuccess

    fun resetPasswordUpdateSuccess(){
        _passwordUpdateSuccess.value = false
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

    fun updatePassword(newPassword: String){
        viewModelScope.launch {
            when (val temp = userDB.updatePassword(newPassword)) {
                "success" -> _passwordUpdateSuccess.value = true
                "invalidUser" -> _invalidUser.value = true
                "triggerReAuth" -> _triggerReAuth.value = true
                else -> _weakPassword.value = temp
            }
        }
    }

    fun signOut(){
        userDB.signOut()
        _readyToNavToSignIn.value = true
    }

    fun deleteUser(){
        viewModelScope.launch {
            when (userDB.deleteUser()) {
                "navToSignIn" -> _readyToNavToSignIn.value = true
                "triggerReAuth" -> _triggerReAuth.value = true
                "invalidUser" -> _invalidUser.value = true
            }
        }
    }

    fun reAuthUser(password: String){
        viewModelScope.launch {
            if (!userDB.reAuthUser(password)) {
                _invalidUser.value = true
            }
        }
    }
}

class SettingsViewModelFactory(private val userDB: UserDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SettingsViewModel(userDB) as T
}