package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.UserDBInterface
import kotlinx.coroutines.launch

class SignUpViewModel(private val userDB: UserDBInterface): ViewModel(){

    private val _email = mutableStateOf("")
    val email: MutableState<String>
        get() = _email

    fun updateInputEmail(newEmail: String){
        _email.value = newEmail
    }

    private val _password1 = mutableStateOf("")
    val password1: MutableState<String>
        get() = _password1

    fun updateInputPassword1(newPassword: String){
        _password1.value = newPassword
    }

    private val _password2 = mutableStateOf("")
    val password2: MutableState<String>
        get() = _password2

    fun updateInputPassword2(newPassword: String){
        _password2.value = newPassword
    }

    /* event for navigating to email verification*/
    private val _signedIn = mutableStateOf(false)
    val signedIn: MutableState<Boolean>
        get() = _signedIn

    fun resetSignedIn(){
        _signedIn.value = false
    }

    /* event/message for error with sign Up*/
    private val _signUpErrorMessage = mutableStateOf<Any?>(null)
    val signUpErrorMessage: MutableState<Any?>
        get() = _signUpErrorMessage

    fun resetSignUpErrorMessage(){
        _signUpErrorMessage.value = null
    }

    /*
    * when the sign up button is clicked either:
    * 1) it works as expected, the account is made and all the setup is performed
    * 2) it completely fails and they can just click again to do #1
    * 3) the account was created, but the setup failed on previous click, so just need to perform the setup
    * */
    fun signUpClick(email: String, password: String){
        viewModelScope.launch{
            userDB.onSignUpClick(email, password, _signedIn, _signUpErrorMessage)
        }
    }

}

class SignupViewModelFactory(private val userDB: UserDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SignUpViewModel(userDB) as T
}