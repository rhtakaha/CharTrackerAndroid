package com.chartracker.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UpdatePasswordViewModel : ViewModel() {
    private val tag = "UpdatePassVM"
    private val user = Firebase.auth.currentUser

    private val _updatePasswordToSettingsNavigate = MutableLiveData<Boolean>()
    val updatePasswordToSettingsNavigate: LiveData<Boolean>
        get() = _updatePasswordToSettingsNavigate

    private fun onUpdatePasswordToSettingsNavigate(){
        _updatePasswordToSettingsNavigate.value = true
    }

    fun onUpdatePasswordToSettingsNavigateComplete(){
        _updatePasswordToSettingsNavigate.value = false
    }

    fun updatePassword(newPassword: String){
        user!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(tag, "User password updated.")
                    onUpdatePasswordToSettingsNavigate()
                }
            }

    }
}