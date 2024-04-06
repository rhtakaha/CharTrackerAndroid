package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chartracker.database.UserDBInterface
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignUpViewModel(private val userDB: UserDBInterface): ViewModel(){
    val auth = Firebase.auth

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

    fun signUpUserWithEmailPassword(email: String, password: String){
        viewModelScope.launch{
            val output = userDB.signUpUserWithEmailPassword(email, password)
            if (output is String || output is Int){
                // if it is an error message
                _signUpErrorMessage.value = output
            }else if (output == true){
                //event for navigating out
                _signedIn.value = true
            }
        }
    }

}

class SignupViewModelFactory(private val userDB: UserDBInterface) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SignUpViewModel(userDB) as T
}