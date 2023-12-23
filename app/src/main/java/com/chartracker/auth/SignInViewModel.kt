package com.chartracker.auth

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignInViewModel(private val application: Application) : AndroidViewModel(application) {
    private val tag = "SignInVM"
    private val auth = Firebase.auth

    private  var credentialManager = CredentialManager.create(application)

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

    private val _signInToSignUpNavigate = MutableLiveData<Boolean>()
    val signInToSignUpNavigate: LiveData<Boolean>
        get() = _signInToSignUpNavigate

    fun onSignInToSignUpNavigate(){
        Log.i(tag, "sign in to sign up nav initiated")
        _signInToSignUpNavigate.value = true
    }

    fun onSignInToSignUpNavigateComplete(){
        Log.i(tag, "sign in to sign up nav completed")
        _signInToSignUpNavigate.value = false
    }

    private val _signInFailed = MutableLiveData<Boolean>()
    val signInFailed: LiveData<Boolean>
        get() = _signInFailed

    private fun onSignInFailed(){
        Log.i(tag, "sign in failed initiated")
        _signInFailed.value = true
    }

    fun onSignInFailedComplete(){
        Log.i(tag, "sign in failed completed")
        _signInFailed.value = false
    }

    fun signInWithEmailPassword(email: String, password: String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(tag, "signInWithEmail:success")

                    // go to their stories page
                    onSignInNavigate()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(tag, "signInWithEmail:failure", task.exception)
                    onSignInFailed()
                }
            }
    }

    //NOTE: NOT 100% IF THIS WORKS SINCE DON'T HAVE A PASSWORD MANAGER SETUP
    fun signInWithSavedCredentials(){
        val getCredRequest = GetCredentialRequest(listOf(GetPasswordOption()))
        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(
                    // Use an activity-based context to avoid undefined system UI
                    // launching behavior.
                    context = application,
                    request = getCredRequest
                )
                handleSignIn(result)
            } catch (e : GetCredentialException) {
                if (e is NoCredentialException){
                    Toast.makeText(application, "No credentials available", Toast.LENGTH_LONG).show()
                }else{
                    Log.e(tag, "Credential exception: ", e)
                }
            }
        }
    }

    //NOTE: NOT 100% IF THIS WORKS SINCE DON'T HAVE A PASSWORD MANAGER SETUP
    private fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.

        when (val credential = result.credential) {
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                // Use id and password to send to your server to validate
                // and authenticate
                signInWithEmailPassword(username, password)
            }
            is CustomCredential -> {
                // If you are also using any external sign-in libraries, parse them
                // here with the utility functions provided.
//                if (credential.type == ExampleCustomCredential.TYPE)  {
//                    try {
//                        val ExampleCustomCredential = ExampleCustomCredential.createFrom(credential.data)
//                        // Extract the required credentials and complete the authentication as per
//                        // the federated sign in or any external sign in library flow
//                    } catch (e: ExampleCustomCredential.ExampleCustomCredentialParsingException) {
//                        // Unlikely to happen. If it does, you likely need to update the dependency
//                        // version of your external sign-in library.
//                        Log.e(tag, "Failed to parse an ExampleCustomCredential", e)
//                    }
//                } else {
//                    // Catch any unrecognized custom credential type here.
//                    Log.e(tag, "Unexpected type of credential")
//                }
            } else -> {
            // Catch any unrecognized credential type here.
            Log.e(tag, "Unexpected type of credential")
        }
        }
    }
}