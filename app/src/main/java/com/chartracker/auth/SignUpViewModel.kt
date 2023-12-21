package com.chartracker.auth

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.chartracker.database.DatabaseAccess
import com.chartracker.database.UserEntity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel(private val application: Application) : AndroidViewModel(application) {
    private val tag = "SignUpVM"
    private val auth = Firebase.auth
    private val db = DatabaseAccess()
    private  var credentialManager = CredentialManager.create(application)

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
                    viewModelScope.launch {
                        saveCredentials(email, password)
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

    //NOTE: NOT 100% IF THIS WORKS SINCE DON'T HAVE A PASSWORD MANAGER SETUP
    private suspend fun saveCredentials(email: String, password: String){
        try {
            //Ask the user for permission to add the credentials to their store
            credentialManager.createCredential(
                request = CreatePasswordRequest(email, password),
                context = application,
            )
            Log.i("CredentialTest", "Credentials successfully added")
        }
        catch (e: CreateCredentialCancellationException) {
            //do nothing, the user chose not to save the credential
            Log.i("CredentialTest", "User cancelled the save")
        }
        catch (e: CreateCredentialNoCreateOptionException){
            Log.i("CredentialTest", "no option to save credentials")
        }
        catch (e: CreateCredentialException) {
            Log.i("CredentialTest", "Credential save error", e)
            Toast.makeText(application, "Error saving credentials", Toast.LENGTH_LONG).show()
        }

    }
}