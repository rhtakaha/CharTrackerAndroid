package com.chartracker.database

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
import kotlinx.coroutines.tasks.await
import timber.log.Timber


interface UserDBInterface {

    suspend fun reAuthUser(password: String): Boolean

    suspend fun updatePassword(newPassword: String): String

    suspend fun updateUserEmail(newEmail: String): Boolean

    fun isSignedIn(): Boolean

    fun signOut()

    suspend fun sendPasswordResetEmail(email: String): Boolean

    suspend fun signInWithEmailPassword(email: String, password: String): Boolean

    suspend fun signUpUserWithEmailPassword(email: String, password: String): Any?

    /*creates a new user in Firebase
    * this includes the overall user
    *               AND
    * the story titles document in the stories collection used to ensure unique titles*/
    suspend fun createUser(user: UserEntity): Boolean

    suspend fun deleteUser(): String

    fun deleteUserData(userUID: String)
}

class UserDB : UserDBInterface {
    private val tag = "dbAccess"
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

    override suspend fun reAuthUser(password: String): Boolean{
        val user = auth.currentUser!!
        var ret = true
        try {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Timber.tag(tag).d("User re-authenticated.")
                    }
                }
                .await()
        }catch (exception: Exception){
            if (exception is FirebaseAuthInvalidUserException ||
                exception is FirebaseAuthInvalidCredentialsException){
                // not really the same error but second seems like it would have same outcome
                ret = false
            }
        }

        return ret
    }

    /*updates user password and on success returns "success"
    * on failure returns:
    * "invalidUser" if FirebaseAuthInvalidUserException
    * "triggerReAuth" if FirebaseAuthRecentLoginRequiredException
    * or the message if FirebaseAuthWeakPasswordException*/
    override suspend fun updatePassword(newPassword: String): String{
        var ret = "success"
        try {
            auth.currentUser!!.updatePassword(newPassword)
                .addOnCompleteListener {
                }
                .await()
        }catch (exception: Exception){
            // If sign in fails, display a message to the user.
            when (exception){
                is FirebaseAuthInvalidUserException -> ret = "invalidUser"
                is FirebaseAuthWeakPasswordException -> ret = exception.message.toString()
                is FirebaseAuthRecentLoginRequiredException -> ret = "triggerReAuth"
            }
        }

        return ret
    }

    override suspend fun updateUserEmail(newEmail: String): Boolean{
        var ret = true
        try {
            auth.currentUser!!.verifyBeforeUpdateEmail(newEmail)
                .addOnCompleteListener {
                }
                .await()
        }catch (exception: Exception){
            //no exceptions listed in documentation
            Timber.tag(tag).d(exception, "failed to update email")
            if (exception is FirebaseAuthInvalidUserException){
                ret = false
            }
        }

        return ret
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
    override suspend fun sendPasswordResetEmail(email: String): Boolean{
        var ret = false
        try {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        ret = true
                        Timber.tag(tag).d("Email sent.")
                    }
                }
                .await()
        }catch (exception: Exception){
            //if the email was not a valid user
            // Don't say anything to protect vs email enumeration attacks
        }

        return ret
    }

    /* signs in the user with the email and password
    * on success returns true
    * else false*/
    override suspend fun signInWithEmailPassword(email: String, password: String): Boolean{
        var ret = true
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        //check if their email is verified
                        if (auth.currentUser!!.isEmailVerified){
                            // go to their stories page
                            Timber.tag(tag).d("email verified!")
                        }else{
                            // if their email is unverified
                            //TODO DOUBLE CHECK IF WE WANTED TO NAV TO EMAIL VERIFY
                            Timber.tag(tag).d("email unverified")
                        }

                    }
                }
                .await()
        }catch (exception: Exception){
            // If sign in fails, display a message to the user.
            Timber.tag(tag).i("fails: %s", exception.toString())
            if (exception is FirebaseAuthInvalidUserException ||
                exception is FirebaseAuthInvalidCredentialsException
            ){
                //email and/or password is incorrect
                //not differentiating to protect vs email enumeration attacks
                ret = false
            }
        }

        return ret
    }

    /*
    * signing up user with email and password
    * return true means it worked
    * return false means it technically worked, but could not setup the account in db
    * return string or string resource means sign up failed*/
    override suspend fun signUpUserWithEmailPassword(email: String, password: String): Any?{
        var ret: Any? = null
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        Timber.tag(tag).d("createUserWithEmail:success")
                        CoroutineScope(Dispatchers.IO).launch {
                            //try and set up their document here, TODO mitigate failure -do again later?
                            ret = createUser(UserEntity(auth.currentUser?.email))

                        }
                    }
                }
                .await()
        }catch (exception: Exception){
            // If sign in fails, display a message to the user.
            Timber.tag(tag).w(exception, "createUserWithEmail:failure")
            ret = when(exception){
                is FirebaseAuthWeakPasswordException -> exception.message.toString()
                is FirebaseAuthInvalidCredentialsException -> R.string.malformed_email_message
                is FirebaseAuthUserCollisionException -> R.string.user_collision_message
                else -> null
            }
        }

        return ret
    }

    /*creates a new user in Firebase
    * this includes the overall user
    *               AND
    * the story titles document in the stories collection used to ensure unique titles*/
    override suspend fun createUser(user: UserEntity): Boolean{
        var ret = true

        try {
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
                .await()
        }catch (exception: Exception){
            ret = false
            Timber.tag(tag).w(exception, "Error setting up user")
        }
        return ret
    }

    /* deletes user and their data and on success returns "navToSignIn"
    * on failure returns:
    * "invalidUser" if FirebaseAuthInvalidUserException
    * "triggerReAuth" if FirebaseAuthRecentLoginRequiredException*/
    override suspend fun deleteUser(): String{
        var ret = "navToSignIn"
        try {
            val user = auth.currentUser!!
            val userUID = user.uid
            user.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        deleteUserData(userUID)

                        Timber.tag(tag).d("User account deleted.")
                    }

                }
                .await()
        }catch (exception: Exception){
            if (exception is FirebaseAuthRecentLoginRequiredException){
                ret = "triggerReAuth"
            }else if (exception is FirebaseAuthInvalidUserException){
                ret = "invalidUser"
            }
        }

        return ret

    }

    /*TODO figure out how best to handle the stories and characters subcollection
    *  AND STORAGE - Firebase extension should be able to do this*/
    override fun deleteUserData(userUID: String){
        db.collection("users")
            .document(userUID)
            .delete()
            .addOnSuccessListener { Timber.tag(tag).d("User Data successfully deleted!") }
            .addOnFailureListener { e -> Timber.tag(tag).w(e, "Error deleting user Data") }
    }


}