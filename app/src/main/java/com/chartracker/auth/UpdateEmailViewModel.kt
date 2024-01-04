package com.chartracker.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UpdateEmailViewModel : ViewModel() {
    val tag = "UpdateEmailVM"

    val user = Firebase.auth.currentUser

    private val _updateEmailToSettingsNavigate = MutableLiveData<Boolean>()
    val updateEmailToSettingsNavigate: LiveData<Boolean>
        get() = _updateEmailToSettingsNavigate

    fun onUpdateEmailToSettingsNavigate(){
        _updateEmailToSettingsNavigate.value = true
    }

    fun onUpdateEmailToSettingsNavigateComplete(){
        _updateEmailToSettingsNavigate.value = false
    }

    //sends the user a verification email to the new email which completes the change
    fun updateUserEmail(newEmail: String){
        user!!.verifyBeforeUpdateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "User email address updated.")
                }
            }
    }
}