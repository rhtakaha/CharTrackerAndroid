package com.chartracker.viewmodels.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.chartracker.R
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.UserEntity
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel(){
    private val tag = "SignUpVM"
    val auth = Firebase.auth
    private val db = DatabaseAccess()

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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(tag, "createUserWithEmail:success")
                    CoroutineScope(Dispatchers.IO).launch {
                        //try and set up their document here, TODO mitigate failure -do again later?
                        db.createUser(UserEntity(auth.currentUser?.email))

                    }

                    //event for navigating out
                    _signedIn.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    val exception = task.exception
                    Log.w(tag, "createUserWithEmail:failure", exception)
                    _signUpErrorMessage.value = when(exception){
                        is FirebaseAuthWeakPasswordException -> exception.message.toString()
                        is FirebaseAuthInvalidCredentialsException -> R.string.malformed_email_message
                        is FirebaseAuthUserCollisionException -> R.string.user_collision_message
                        else -> null
                    }
                }
            }
    }

}