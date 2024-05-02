package com.chartracker.database

import androidx.compose.runtime.MutableState
import com.chartracker.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


interface UserDBInterface {

    suspend fun reAuthUser(password: String, invalidUser: MutableState<Boolean>)

    suspend fun updatePassword(
        newPassword: String,
        passwordUpdateSuccess: MutableState<Boolean>,
        weakPassword: MutableState<String>,
        triggerReAuth: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>)

    suspend fun updateUserEmail(
        newEmail: String,
        updateEmailVerificationSent: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>)

    fun isSignedIn(): Boolean

    fun signOut()

    suspend fun sendPasswordResetEmail(email: String, emailSent: MutableState<Boolean>)
    suspend fun signInWithEmailPassword(
        email: String,
        password: String,
        signedIn: MutableState<Boolean>,
        unverifiedEmail: MutableState<Boolean>,
        invalidCredentials: MutableState<Boolean>)

    suspend fun signUpUserWithEmailPassword(
        email: String,
        password: String,
        signedIn: MutableState<Boolean>,
        signUpErrorMessage: MutableState<Any?>)

    suspend fun deleteUser(
        test: String?,
        readyToNavToSignIn: MutableState<Boolean>,
        triggerReAuth: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>)

    suspend fun sendVerificationEmail(emailSent: MutableState<Boolean>)

    suspend fun isEmailVerified(verified: MutableState<Boolean>)
}

class UserDB : UserDBInterface {
    private val tag = "userDB"
    private val db = Firebase.firestore
    private val auth = Firebase.auth

//    fun enableEmulatorTesting(){
//        Timber.tag(tag).i("ENABLING EMULATORS")
//        db.useEmulator("10.0.2.2", 8080)
//        auth.useEmulator("10.0.2.2", 9099)
//        Firebase.storage.useEmulator("10.0.2.2", 9199)
//
//
//        db.firestoreSettings = firestoreSettings {
//            isPersistenceEnabled = false
//        }
//    }

    /*****************************************    USER  *******************************************/

    override suspend fun reAuthUser(password: String, invalidUser: MutableState<Boolean>){
        val user = auth.currentUser!!

        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                Timber.tag(tag).d("User re-authenticated.")
            }
            .addOnFailureListener {exception ->
                if (exception is FirebaseAuthInvalidUserException ||
                    exception is FirebaseAuthInvalidCredentialsException){
                    // not really the same error but second seems like it would have same outcome
                    invalidUser.value = true
                }
            }
    }

    /*updates user password and on success sets passwordUpdateSuccess state to true
    * on failure:
    * invalidUser.value = true if FirebaseAuthInvalidUserException
    * triggerReAuth.value = true if FirebaseAuthRecentLoginRequiredException
    * weakPassword state is set to the message if FirebaseAuthWeakPasswordException*/
    override suspend fun updatePassword(
        newPassword: String,
        passwordUpdateSuccess: MutableState<Boolean>,
        weakPassword: MutableState<String>,
        triggerReAuth: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>){

        auth.currentUser!!.updatePassword(newPassword)
            .addOnSuccessListener {
                passwordUpdateSuccess.value = true
            }
            .addOnFailureListener { exception ->
                // If update password fails, display a message to the user.
                when (exception){
                    is FirebaseAuthInvalidUserException -> invalidUser.value = true
                    is FirebaseAuthWeakPasswordException -> weakPassword.value = exception.message.toString()
                    is FirebaseAuthRecentLoginRequiredException -> triggerReAuth.value = true
                }
            }
    }

    override suspend fun updateUserEmail(
        newEmail: String,
        updateEmailVerificationSent: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>){

        auth.currentUser!!.verifyBeforeUpdateEmail(newEmail)
            .addOnSuccessListener {
                updateEmailVerificationSent.value = true
            }
            .addOnFailureListener {exception ->
                //no exceptions listed in documentation
                Timber.tag(tag).d(exception, "failed to update email")
                if (exception is FirebaseAuthInvalidUserException){
                    invalidUser.value = true
                }
            }
    }

    /* returns true if signed in
    * else false*/
    override fun isSignedIn(): Boolean {
        return auth.currentUser != null
    }

    override fun signOut(){
        auth.signOut()
        Timber.tag(tag).i("signed out!")
    }

    /* sends password reset to given email*/
    override suspend fun sendPasswordResetEmail(email: String, emailSent: MutableState<Boolean>){
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Timber.tag(tag).d("Email sent.")
                emailSent.value = true
            }
            .addOnFailureListener {
                //if the email was not a valid user
                // Don't say anything to protect vs email enumeration attacks
            }
    }

    /* signs in the user with the email and password
    * on success returns "success"
    *   (on success) if the email is not verified returns "unverified"
    * else "failure"*/
    override suspend fun signInWithEmailPassword(
        email: String,
        password: String,
        signedIn: MutableState<Boolean>,
        unverifiedEmail: MutableState<Boolean>,
        invalidCredentials: MutableState<Boolean>){

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //check if their email is verified
                if (auth.currentUser!!.isEmailVerified){
                    // go to their stories page
                    Timber.tag(tag).d("email verified!")
                    signedIn.value = true
                }else{
                    // if their email is unverified
                    Timber.tag(tag).d("email unverified")
                    unverifiedEmail.value = true
                }
            }
            .addOnFailureListener {exception ->
                // If sign in fails, display a message to the user.
                Timber.tag(tag).i("fails: %s", exception.toString())
                if (exception is FirebaseAuthInvalidUserException ||
                    exception is FirebaseAuthInvalidCredentialsException
                ){
                    //email and/or password is incorrect
                    //not differentiating to protect vs email enumeration attacks
                    invalidCredentials.value = true
                }
            }
    }

    /*
    * signing up user with email and password
    * return true means it worked
    * return false means it technically worked, but could not setup the account in db
    * return string or string resource means sign up failed*/
    override suspend fun signUpUserWithEmailPassword(
        email: String,
        password: String,
        signedIn: MutableState<Boolean>,
        signUpErrorMessage: MutableState<Any?>){

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Sign in success
                Timber.tag(tag).d("createUserWithEmail:success")
                CoroutineScope(Dispatchers.IO).launch {
                    //try and set up their document here, TODO mitigate failure -do again later?
                    createUser(UserEntity(auth.currentUser?.email), signedIn)
                }
            }
            .addOnFailureListener {exception ->
                // If sign in fails, display a message to the user.
                Timber.tag(tag).w(exception, "createUserWithEmail:failure")
                when(exception){
                    is FirebaseAuthWeakPasswordException -> signUpErrorMessage.value = exception.message.toString()
                    is FirebaseAuthInvalidCredentialsException -> signUpErrorMessage.value =  R.string.malformed_email_message
                    is FirebaseAuthUserCollisionException -> signUpErrorMessage.value =  R.string.user_collision_message
                    else -> signUpErrorMessage.value =  R.string.unexpected_exception
                }
            }
    }

    /*creates a new user in Firebase
    * this includes the overall user
    *               AND
    * the story titles document in the stories collection used to ensure unique titles
    * handles the state for signed in*/
    /** Can call if on sign in too to mitigate failure?**/
    private fun createUser(user: UserEntity, signedIn: MutableState<Boolean>){

        // batched operation so atomic
        db.runBatch {batch ->

            //create user
            batch.set(
                db.collection("users")
                    .document(auth.currentUser!!.uid),
                user
            )

            // create titles document
            batch.set(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document("titles"),
                hashMapOf("titles" to listOf<String>())
            )

        }
            .addOnSuccessListener {
                signedIn.value = true
            }
            .addOnFailureListener { exception ->
                Timber.tag(tag).w(exception, "Error setting up user")
                /** EXPONENTIAL BACKOFF?????**/
            }
    }

    /* deletes user and their data and on success sets readyToNavToSignIn state to true
    * on failure:
    * invalidUser.value = true if FirebaseAuthInvalidUserException
    * triggerReAuth.value = true if FirebaseAuthRecentLoginRequiredException
    * (test parameter is entirely for testing with no practical function)*/
    override suspend fun deleteUser(
        test: String?,
        readyToNavToSignIn: MutableState<Boolean>,
        triggerReAuth: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>){
        val user = auth.currentUser!!
        val userUID = user.uid
        user.delete()
            .addOnSuccessListener {
                deleteUserData(userUID, readyToNavToSignIn)
            }
            .addOnFailureListener {exception ->
                if (exception is FirebaseAuthRecentLoginRequiredException){
                    triggerReAuth.value = true
                }else if (exception is FirebaseAuthInvalidUserException){
                    invalidUser.value = true
                }
            }
    }

    /*TODO figure out how best to handle the stories and characters subcollection
    *  AND STORAGE - Firebase extension should be able to do this*/
    private fun deleteUserData(userUID: String, readyToNavToSignIn: MutableState<Boolean>){
        db.collection("users")
            .document(userUID)
            .delete()
            .addOnSuccessListener {
                Timber.tag(tag).d("User Data successfully deleted!")
                readyToNavToSignIn.value = true
            }
            .addOnFailureListener { e ->
                Timber.tag(tag).w(e, "Error deleting user Data")
            }
    }

    /*
    * sends the verification email to the email provided by the user*/
    override suspend fun sendVerificationEmail(emailSent: MutableState<Boolean>) {
        auth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {
                Timber.tag(tag).d("Email sent.")
                emailSent.value = true
            }
            .addOnFailureListener {exception ->
                Timber.tag(tag).w(exception, "Email failed to send!")
            }
    }

    /*refreshes the current user and checks if the email is verified*/
    override suspend fun isEmailVerified(verified: MutableState<Boolean>){
        auth.currentUser!!.reload()
            .addOnSuccessListener {
                verified.value = auth.currentUser!!.isEmailVerified
            }
            .addOnFailureListener {
            }
    }
}

class MockUserDB: UserDBInterface{
    /* sets invalidUser state to true if password is not "password"
    * otherwise (such as if password is "password") invalidUser remains false*/
    override suspend fun reAuthUser(password: String, invalidUser: MutableState<Boolean>) {
        invalidUser.value = password != "password"
    }

    /*
    * if newPassword is:
    * "reauth" -> triggerReAuth.value = true
    * "new" -> passwordUpdateSuccess.value = true
    * "weak" -> weakPassword.value = "weak password"
    * else ->  invalidUser.value = true*/
    override suspend fun updatePassword(
        newPassword: String,
        passwordUpdateSuccess: MutableState<Boolean>,
        weakPassword: MutableState<String>,
        triggerReAuth: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>) {
        when (newPassword){
            "reauth" -> triggerReAuth.value = true
            "new" -> passwordUpdateSuccess.value = true
            "weak" -> weakPassword.value = "weak password"
            else ->  invalidUser.value = true
        }
    }

    /*
    * if newEmail is "new" sets updateEmailVerificationSent state to true
    * else invalidUser state is set to true*/
    override suspend fun updateUserEmail(
        newEmail: String,
        updateEmailVerificationSent: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>) {
        if (newEmail == "new"){
            updateEmailVerificationSent.value = true
        }else{
            invalidUser.value = true
        }
    }

    /*always returns false since not bypassing the screen during tests*/
    override fun isSignedIn(): Boolean {
        return false
    }

    override fun signOut() {
        // no return
    }

    /* if email is "email" sets the state to true
    * else false*/
    override suspend fun sendPasswordResetEmail(email: String, emailSent: MutableState<Boolean>) {
        emailSent.value = email == "email"
    }

    /*
    * if password is:
    * "correct" -> sets signedIn state to true
    * "unverified" -> sets unverifiedEmail state to true
    * else -> sets invalidCredentials state to true
    * */
    override suspend fun signInWithEmailPassword(
        email: String,
        password: String,
        signedIn: MutableState<Boolean>,
        unverifiedEmail: MutableState<Boolean>,
        invalidCredentials: MutableState<Boolean>) {
        when(password){
            "correct" -> signedIn.value = true
            "unverified" -> unverifiedEmail.value = true
            else -> invalidCredentials.value = true
        }
    }

//    override suspend fun signInWithEmailPassword(email: String, password: String): String {
//        return when(password){
//            "correct" -> "success"
//            "unverified" -> "unverified"
//            else -> "failure"
//        }
//    }

    /*
    * when email is:
    * "email" -> sets signedIn state to true
    * "collide" -> sets signUpErrorMessage stat to R.string.user_collision_message
    * "weak" -> sets signUpErrorMessage stat to "weak password"
    * "invalid" -> sets signUpErrorMessage stat to R.string.malformed_email_message
    * else -> sets signUpErrorMessage stat to R.string.unexpected_exception*/
    override suspend fun signUpUserWithEmailPassword(
        email: String,
        password: String,
        signedIn: MutableState<Boolean>,
        signUpErrorMessage: MutableState<Any?>) {
        when(email){
            "email" -> signedIn.value = true
            "collide" -> signUpErrorMessage.value =  R.string.user_collision_message
            "weak" -> signUpErrorMessage.value = "weak password"
            "invalid" -> signUpErrorMessage.value = R.string.malformed_email_message
            else -> signUpErrorMessage.value =  R.string.unexpected_exception
        }
    }

    /*
    * if test is:
    * "reauth" -> "triggerReAuth"
    * "gone" -> "navToSignIn"
    *  else-> "invalidUser"
    * */
    override suspend fun deleteUser(
        test: String?,
        readyToNavToSignIn: MutableState<Boolean>,
        triggerReAuth: MutableState<Boolean>,
        invalidUser: MutableState<Boolean>) {
        when(test){
            "reauth" -> triggerReAuth.value = true
            "invalid" -> invalidUser.value = true
             else -> readyToNavToSignIn.value = true
        }
    }

    /*
    * always sets emailSent state to true*/
    override suspend fun sendVerificationEmail(emailSent: MutableState<Boolean>) {
        emailSent.value = true
    }

    /*
    * always sets verified to true*/
    override suspend fun isEmailVerified(verified: MutableState<Boolean>) {
        verified.value = true
    }
}