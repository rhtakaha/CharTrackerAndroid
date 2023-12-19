package com.chartracker.util

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsViewModel : ViewModel() {
    val tag = "settingsVM"
    private val auth = Firebase.auth

    fun signOut(){
        if (auth.currentUser != null) {
            auth.signOut()
        }else{
            Log.i(tag, "not signed in so cannot sign out")
        }
    }
}