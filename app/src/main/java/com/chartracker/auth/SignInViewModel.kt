package com.chartracker.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignInViewModel : ViewModel() {
    private val tag = "SignInVM"

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
}