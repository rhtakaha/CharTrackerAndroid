package com.chartracker.ui.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

    fun sendPasswordResetEmail(email: String){
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "Email sent.")
                }
            }
    }

    fun signInWithEmailPassword(email: String, password: String){
        Log.d(tag, "email: $email password: $password")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")

                    //check if their email is verified
                    if (auth.currentUser!!.isEmailVerified){
                        // go to their stories page
                        Log.d(tag, "email verified!")
                    }else{
                        // if their email is unverified
                        Log.d(tag, "email unverified")
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                }
            }
    }
}