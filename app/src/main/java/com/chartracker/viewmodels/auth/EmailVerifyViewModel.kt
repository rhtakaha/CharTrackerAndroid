package com.chartracker.viewmodels.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class EmailVerifyViewModel : ViewModel(){
    private val tag = "EmailVerifyVM"
    val user = Firebase.auth.currentUser

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
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.tag(tag).d("Email sent.")
                    _emailSent.value = true
                }else{
                    Timber.tag(tag).w(task.exception, "Email failed to send!")
                }
            }
    }

    /*refreshes the current user and checks if the email is verified*/
    fun isEmailVerified(): Boolean{
        return try {
            user!!.reload()
            Timber.tag(tag).i("Is email verified? %s", user.isEmailVerified)
            user.isEmailVerified
        }catch (e: Exception){
            if (e is FirebaseAuthInvalidUserException){
                _failedReload.value = true
            }
            false
        }
    }
}