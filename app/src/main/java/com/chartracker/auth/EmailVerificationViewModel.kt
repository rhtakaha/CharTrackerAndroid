package com.chartracker.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerificationViewModel : ViewModel() {
    val tag = "EmailVerificationVM"

    val user = Firebase.auth.currentUser

    private val _emailVerificationToStoriesNavigate = MutableLiveData<Boolean>()
    val emailVerificationToStoriesNavigate: LiveData<Boolean>
        get() = _emailVerificationToStoriesNavigate

    fun onEmailVerificationToStoriesNavigate(){
        _emailVerificationToStoriesNavigate.value = true
    }

    fun onEmailVerificationToStoriesNavigateComplete(){
        _emailVerificationToStoriesNavigate.value = false
    }

    private val _emailVerificationToUpdateEmailNavigate = MutableLiveData<Boolean>()
    val emailVerificationToUpdateEmailNavigate: LiveData<Boolean>
        get() = _emailVerificationToUpdateEmailNavigate

    fun onEmailVerificationToUpdateEmailNavigate(){
        _emailVerificationToUpdateEmailNavigate.value = true
    }

    fun onEmailVerificationToUpdateEmailNavigateComplete(){
        _emailVerificationToUpdateEmailNavigate.value = false
    }


    /* once on entry and then when the button it pressed*/
    fun sendVerificationEmail(){
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Email sent.")
                }
            }
    }

    fun isEmailVerified(): Boolean{
        user!!.reload()
        return user.isEmailVerified

    }
}