package com.chartracker.viewmodels.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel(){
    private val tag = "SettingsViewModel"
    private val auth = Firebase.auth
    private val user = Firebase.auth.currentUser!!

    private val _updatedEmail = mutableStateOf("")
    val updatedEmail: MutableState<String>
        get() = _updatedEmail

    fun updateUpdatedEmail(new: String){
        _updatedEmail.value = new
    }

    private val _updatedPassword = mutableStateOf("")
    val updatedPassword: MutableState<String>
        get() = _updatedPassword

    fun updateUpdatedPassword(new: String){
        _updatedPassword.value = new
    }

    private val _confirmedPassword = mutableStateOf("")
    val confirmedPassword: MutableState<String>
        get() = _confirmedPassword

    fun updateConfirmedPassword(new: String){
        _confirmedPassword.value = new
    }


//    /*navigate to email verify event*/
//    private val _readyToNavToEmailVerify = mutableStateOf(false)
//    val readyToNavToEmailVerify: MutableState<Boolean>
//        get() = _readyToNavToEmailVerify
//
//    fun resetReadyToNavToEmailVerify(){
//        _readyToNavToEmailVerify.value = false
//    }

    /*navigate to sign in event (on sign out or account delete)*/
    private val _readyToNavToSignIn = mutableStateOf(false)
    val readyToNavToSignIn: MutableState<Boolean>
        get() = _readyToNavToSignIn

    fun resetReadyToNavToSignIn(){
        _readyToNavToSignIn.value = false
    }

    //sends the user a verification email to the new email which completes the change
    fun updateUserEmail(newEmail: String){
        user.verifyBeforeUpdateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "User email address updated.")
                }
            }
    }

    fun updatePassword(newPassword: String){
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "User password updated.")
                }
            }

    }

    fun signOut(){
        auth.signOut()
        _readyToNavToSignIn.value = true
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
                    _readyToNavToSignIn.value = true
                } else if (task.exception is FirebaseAuthRecentLoginRequiredException){
                    Log.i(tag, "failed: ${task.exception}")
                }
            }

    }

    //TODO need to move this into a nice pop up and be reusable for all the ops that could require
    //  (changing email, changing password, and here with account deletion)
    fun reAuthUser(password: String){
        Log.i(tag, "email: ${user.email} password: $password")
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task -> if (task.isSuccessful){ Log.d(tag, "User re-authenticated.")}else {Log.d(tag, "failed to reauth")} }
    }
}