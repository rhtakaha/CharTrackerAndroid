package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.UserDBInterface
import kotlinx.coroutines.launch
import timber.log.Timber

class SignInViewModel(private val userDB: UserDBInterface) : ViewModel(){
    private val _email = mutableStateOf("")
    val email: MutableState<String>
        get() = _email

    fun updateInputEmail(newEmail: String){
        _email.value = newEmail
    }

    private val _password = mutableStateOf("")
    val password: MutableState<String>
        get() = _password

    fun updateInputPassword(newPassword: String){
        _password.value = newPassword
    }

    /* event for navigating to stories*/
    private val _signedIn = mutableStateOf(false)
    val signedIn: MutableState<Boolean>
        get() = _signedIn

    fun resetSignedIn(){
        _signedIn.value = false
    }

    /* event for displaying incorrect password dialog*/
    private val _invalidCredentials = mutableStateOf(false)
    val invalidCredentials: MutableState<Boolean>
        get() = _invalidCredentials

    fun resetInvalidCredentials(){
        _invalidCredentials.value = false
    }

    /* event for telling user the email was sent IF the email is valid*/
    private val _emailSent = mutableStateOf(false)
    val emailSent: MutableState<Boolean>
        get() = _emailSent

    fun resetEmailSent(){
        _emailSent.value = false
    }

    /* event for notifying that the user's email is unverified*/
    private val _unverifiedEmail = mutableStateOf(false)
    val unverifiedEmail: MutableState<Boolean>
        get() = _unverifiedEmail

    fun resetUnverifiedEmail(){
        _unverifiedEmail.value = false
    }

    init {
        _signedIn.value = userDB.isSignedIn()
    }

    fun sendPasswordResetEmail(email: String){
        viewModelScope.launch {
            _emailSent.value = userDB.sendPasswordResetEmail(email)
        }
    }

    fun signInWithEmailPassword(email: String, password: String){
        Timber.tag("SignIn").i("signing in")
        viewModelScope.launch {
            when(userDB.signInWithEmailPassword(email, password)){
                "success" -> _signedIn.value = true
                "unverified" -> _unverifiedEmail.value = true
                "failure" -> _invalidCredentials.value = true
            }
//            if (!){
//                _invalidCredentials.value = true
//            }else{
//                _signedIn.value = true
//            }
        }
    }
}

class SignInViewModelFactory(private val userDB: UserDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SignInViewModel(userDB) as T
}