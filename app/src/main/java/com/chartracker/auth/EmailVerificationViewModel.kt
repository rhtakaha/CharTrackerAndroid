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

    private val _emailVerificationToSignInNavigate = MutableLiveData<Boolean>()
    val emailVerificationToSignInNavigate: LiveData<Boolean>
        get() = _emailVerificationToSignInNavigate

    private fun onEmailVerificationToSignInNavigate(){
        _emailVerificationToSignInNavigate.value = true
    }

    fun onEmailVerificationToSignInNavigateComplete(){
        _emailVerificationToSignInNavigate.value = false
    }

    fun backToSignIn(){
        // sign out and send user to sign in to get through if their email is verified
        Firebase.auth.signOut()
        onEmailVerificationToSignInNavigate()
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
}