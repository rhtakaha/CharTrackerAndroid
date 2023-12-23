package com.chartracker.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsViewModel : ViewModel() {
    val tag = "settingsVM"
    private val auth = Firebase.auth

    private val _signOutNavigate = MutableLiveData<Boolean>()
    val signOutNavigate: LiveData<Boolean>
        get() = _signOutNavigate

    private fun onSignOutNavigate(){
        _signOutNavigate.value = true
    }

    fun onSignOutNavigateComplete(){
        _signOutNavigate.value = false
    }

    fun signOut(){
        if (auth.currentUser != null) {
            auth.signOut()
            onSignOutNavigate()
        }else{
            Log.i(tag, "not signed in so cannot sign out")
        }
    }
}