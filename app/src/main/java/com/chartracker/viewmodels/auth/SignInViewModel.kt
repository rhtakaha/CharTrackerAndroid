package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class SignInViewModel() : ViewModel(){
    private val tag = "SignInVM"
    val auth = Firebase.auth

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

    init {
        _signedIn.value = auth.currentUser != null

        //ONLY FOR TESTING
//        DatabaseAccess().enableEmulatorTesting()
    }

    fun sendPasswordResetEmail(email: String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                _emailSent.value = true
                if (task.isSuccessful) {
                    Timber.tag(tag).d("Email sent.")
                }else{
                    //if the email was not a valid user
                    // Don't say anything to protect vs email enumeration attacks
                }
            }
    }

    fun signInWithEmailPassword(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.tag(tag).d("signInWithEmail:success")

                    //check if their email is verified
                    if (auth.currentUser!!.isEmailVerified){
                        // go to their stories page
                        Timber.tag(tag).d("email verified!")
                    }else{
                        // if their email is unverified
                        Timber.tag(tag).d("email unverified")
                    }
                    //event for navigating out
                    _signedIn.value = true

                } else {
                    // If sign in fails, display a message to the user.
                    if (task.exception is FirebaseAuthInvalidUserException ||
                        task.exception is FirebaseAuthInvalidCredentialsException){
                        //email and/or password is incorrect
                        //not differentiating to protect vs email enumeration attacks
                        _invalidCredentials.value = true
                    }
                }
            }
    }
}