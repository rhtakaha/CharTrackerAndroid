package com.chartracker.util

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    val tag = "settingsVM"
    private val auth = Firebase.auth
    private val user = auth.currentUser!!

    private val _signOutNavigate = MutableLiveData<Boolean>()
    val signOutNavigate: LiveData<Boolean>
        get() = _signOutNavigate

    private fun onSignOutNavigate(){
        _signOutNavigate.value = true
    }

    fun onSignOutNavigateComplete(){
        _signOutNavigate.value = false
    }

    private val _settingsToUpdateEmailNavigate = MutableLiveData<Boolean>()
    val settingsToUpdateEmailNavigate: LiveData<Boolean>
        get() = _settingsToUpdateEmailNavigate

    fun onSettingsToUpdateEmailNavigate(){
        _settingsToUpdateEmailNavigate.value = true
    }

    fun onSettingsToUpdateEmailNavigateComplete(){
        _settingsToUpdateEmailNavigate.value = false
    }

    private val _settingsToUpdatePasswordNavigate = MutableLiveData<Boolean>()
    val settingsToUpdatePasswordNavigate: LiveData<Boolean>
        get() = _settingsToUpdatePasswordNavigate

    fun onSettingsToUpdatePasswordNavigate(){
        _settingsToUpdatePasswordNavigate.value = true
    }

    fun onSettingsToUpdatePasswordNavigateComplete(){
        _settingsToUpdatePasswordNavigate.value = false
    }

    private val _settingsToSignInNavigate = MutableLiveData<Boolean>()
    val settingsToSignInNavigate: LiveData<Boolean>
        get() = _settingsToSignInNavigate

    private fun onSettingsToSignInNavigate(){
        _settingsToSignInNavigate.value = true
    }

    fun onSettingsToSignInNavigateComplete(){
        _settingsToSignInNavigate.value = false
    }

    fun signOut(){
        if (auth.currentUser != null) {
            auth.signOut()
            onSignOutNavigate()
        }else{
            Log.i(tag, "not signed in so cannot sign out")
        }
    }

    fun deleteUser(){
        val user = auth.currentUser!!
        val userUID = user.uid
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    viewModelScope.launch {
                        // need to delete user data as well but probably will need to use cloud functions LATER
                        DatabaseAccess().deleteUserData(userUID)
                    }
                    Log.d(tag, "User account deleted.")
                    onSettingsToSignInNavigate()
                } else if (task.exception is FirebaseAuthRecentLoginRequiredException){
                    Log.i(tag, "failed: ${task.exception}")
                }
            }

    }

    //TODO need to move this into a nice pop up and be reusable for all the ops that could require
    //  (changing email, changing password, and here with account deletion)
    fun reauthUser(password: String){
        Log.i(tag, "email: ${user.email} password: $password")
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task -> if (task.isSuccessful){ Log.d(tag, "User re-authenticated.")}else {Log.d(tag, "failed to reauth")} }
    }
}