package com.chartracker.viewmodels.auth

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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

    /*navigate to sign in event (on sign out or account delete)*/
    private val _readyToNavToSignIn = mutableStateOf(false)
    val readyToNavToSignIn: MutableState<Boolean>
        get() = _readyToNavToSignIn

    fun resetReadyToNavToSignIn(){
        _readyToNavToSignIn.value = false
    }

    /*event for email updated (so verification email on the way)*/
    private val _updateEmailVerificationSent = mutableStateOf(false)
    val updateEmailVerificationSent: MutableState<Boolean>
        get() = _updateEmailVerificationSent

    fun resetUpdateEmailVerificationSent(){
        _updateEmailVerificationSent.value = false
    }

    /*event for weak new password*/
    private val _weakPassword = mutableStateOf("")
    val weakPassword: MutableState<String>
        get() = _weakPassword

    fun resetWeakPassword(){
        _weakPassword.value = ""
    }

    /*event for invalid user*/
    private val _invalidUser = mutableStateOf(false)
    val invalidUser: MutableState<Boolean>
        get() = _invalidUser

    fun resetInvalidUser(){
        _invalidUser.value = false
    }

    /*event for triggering reauthentication if user signed in too long ago*/
    private val _triggerReAuth = mutableStateOf(false)
    val triggerReAuth: MutableState<Boolean>
        get() = _triggerReAuth

    fun resetTriggerReAuth(){
        _triggerReAuth.value = false
    }

    /*event for successful password update*/
    private val _passwordUpdateSuccess = mutableStateOf(false)
    val passwordUpdateSuccess: MutableState<Boolean>
        get() = _passwordUpdateSuccess

    fun resetPasswordUpdateSuccess(){
        _passwordUpdateSuccess.value = false
    }

    //sends the user a verification email to the new email which completes the change
    fun updateUserEmail(newEmail: String){
        user.verifyBeforeUpdateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _updateEmailVerificationSent.value = true
                }else{
                    //no exceptions listed in documentation
                    Log.d(tag, "failed to update email", task.exception)
                    if (task.exception is FirebaseAuthInvalidUserException){
                        _invalidUser.value = true
                    }
                }
            }
    }

    fun updatePassword(newPassword: String){
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _passwordUpdateSuccess.value = true
                }else {
                    // If sign in fails, display a message to the user.
                    when (task.exception){
                        is FirebaseAuthInvalidUserException -> _invalidUser.value = true
                        is FirebaseAuthWeakPasswordException -> _weakPassword.value = (task.exception as FirebaseAuthWeakPasswordException).message.toString()
                        is FirebaseAuthRecentLoginRequiredException -> _triggerReAuth.value = true
                    }
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
                } else{
                    if (task.exception is FirebaseAuthRecentLoginRequiredException){
                        _triggerReAuth.value = true
                    }else if (task.exception is FirebaseAuthInvalidUserException){
                        _invalidUser.value = true
                    }
                }

            }

    }

    fun reAuthUser(password: String){
        Log.i(tag, "email: ${user.email} password: $password")
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "User re-authenticated.")
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException ||
                        task.exception is FirebaseAuthInvalidCredentialsException){
                        // not really the same error but second seems like it would have same outcome
                        _invalidUser.value = true
                    }
                }
            }
    }
}