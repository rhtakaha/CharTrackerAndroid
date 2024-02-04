package com.chartracker.viewmodels.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailVerifyViewModel : ViewModel(){
    private val tag = "EmailVerifyVM"
    val user = Firebase.auth.currentUser

    init {
        // sends the initial email when screen is opened
        sendVerificationEmail()
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

    /*refreshes the current user and checks if the email is verified*/
    fun isEmailVerified(): Boolean{
        user!!.reload()
        Log.i(tag, "Is email verified? ${user.isEmailVerified}")
        return user.isEmailVerified
    }
}