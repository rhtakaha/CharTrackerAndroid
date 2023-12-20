package com.chartracker.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInViewModel : ViewModel() {
    private val tag = "SignInVM"
    private val auth = Firebase.auth

    private val _signInNavigate = MutableLiveData<Boolean>()
    val signInNavigate: LiveData<Boolean>
        get() = _signInNavigate

    fun onSignInNavigate(){
        Log.i(tag, "sign in nav initiated")
        _signInNavigate.value = true
    }

    fun onSignInNavigateComplete(){
        Log.i(tag, "sign in nav completed")
        _signInNavigate.value = false
    }

    private val _signInToSignUpNavigate = MutableLiveData<Boolean>()
    val signInToSignUpNavigate: LiveData<Boolean>
        get() = _signInToSignUpNavigate

    fun onSignInToSignUpNavigate(){
        Log.i(tag, "sign in to sign up nav initiated")
        _signInToSignUpNavigate.value = true
    }

    fun onSignInToSignUpNavigateComplete(){
        Log.i(tag, "sign in to sign up nav completed")
        _signInToSignUpNavigate.value = false
    }

    private val _signInToSettingsNavigate = MutableLiveData<Boolean>()
    val signInToSettingsNavigate: LiveData<Boolean>
        get() = _signInToSettingsNavigate

    fun onSignInToSettingsNavigate(){
        Log.i(tag, "sign in to settings nav initiated")
        _signInToSettingsNavigate.value = true
    }

    fun onSignInToSettingsNavigateComplete(){
        Log.i(tag, "sign in to settings nav completed")
        _signInToSettingsNavigate.value = false
    }

    private val _signInFailed = MutableLiveData<Boolean>()
    val signInFailed: LiveData<Boolean>
        get() = _signInFailed

    private fun onSignInFailed(){
        Log.i(tag, "sign in failed initiated")
        _signInFailed.value = true
    }

    fun onSignInFailedComplete(){
        Log.i(tag, "sign in failed completed")
        _signInFailed.value = false
    }

    fun signInWithEmailPassword(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")

                    // go to their stories page
                    onSignInNavigate()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    onSignInFailed()
                }
            }
    }
}