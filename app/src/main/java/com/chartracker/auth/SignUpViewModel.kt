package com.chartracker.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.UserEntity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val tag = "SignUpVM"
    private val auth = Firebase.auth
    private val db = DatabaseAccess()

    private val _signUpNavigate = MutableLiveData<Boolean>()
    val signUpNavigate: LiveData<Boolean>
        get() = _signUpNavigate

    fun onSignUpNavigate(){
        Log.i(tag, "sign up nav initiated")
        _signUpNavigate.value = true
    }

    fun onSignUpNavigateComplete(){
        Log.i(tag, "sign up nav completed")
        _signUpNavigate.value = false
    }

    private val _signUpFailed = MutableLiveData<Boolean>()
    val signUpFailed: LiveData<Boolean>
        get() = _signUpFailed

    private fun onSignUpFailed(){
        Log.i(tag, "sign up failed initiated")
        _signUpFailed.value = true
    }

    fun onSignUpFailedComplete(){
        Log.i(tag, "sign up failed completed")
        _signUpFailed.value = false
    }

    fun signUpUserWithEmailPassword(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(tag, "createUserWithEmail:success")
                    CoroutineScope(Dispatchers.IO).launch {
                        //try and set up their document here, TODO mitigate failure -do again later?
                        db.createUser(UserEntity(auth.currentUser?.email))
                    }
                    onSignUpNavigate()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "createUserWithEmail:failure", task.exception)
                    onSignUpFailed()
                    //TODO need to flush out the user message since things like a non email, email in use, etc
                }
            }
    }
}