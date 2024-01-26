package com.chartracker.ui.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.UserEntity
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
                    //onSignUpNavigate()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "createUserWithEmail:failure", task.exception)
                    //TODO need to flush out the user message since things like a non email, email in use, etc
                }
            }
    }

}