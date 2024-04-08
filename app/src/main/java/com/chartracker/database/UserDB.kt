package com.chartracker.database

import com.chartracker.R
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber


interface UserDBInterface {

    fun reAuthUser(password: String): Boolean

    fun updatePassword(newPassword: String): String

    fun updateUserEmail(newEmail: String): Boolean

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

    fun deleteUser(): String

    fun deleteUserData(userUID: String)
}

class UserDB : UserDBInterface {
    private val tag = "dbAccess"
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    fun enableEmulatorTesting(){
        Timber.tag(tag).i("ENABLING EMULATORS")
        db.useEmulator("10.0.2.2", 8080)
        auth.useEmulator("10.0.2.2", 9099)
        storage.useEmulator("10.0.2.2", 9199)


        db.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false
        }
    }

    /*****************************************    USER  *******************************************/

    override fun reAuthUser(password: String): Boolean{
        val user = auth.currentUser!!
        var ret = true
        val credential = EmailAuthProvider.getCredential(user.email!!, password)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.tag(tag).d("User re-authenticated.")
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException ||
                        task.exception is FirebaseAuthInvalidCredentialsException){
                        // not really the same error but second seems like it would have same outcome
                        ret = false
                    }
                }
            }
        return ret
    }

    /*updates user password and on success returns "success"
    * on failure returns:
    * "invalidUser" if FirebaseAuthInvalidUserException
    * "triggerReAuth" if FirebaseAuthRecentLoginRequiredException
    * or the message if FirebaseAuthWeakPasswordException*/
    override fun updatePassword(newPassword: String): String{
        var ret = "success"
        auth.currentUser!!.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // If sign in fails, display a message to the user.
                    when (task.exception){
                        is FirebaseAuthInvalidUserException -> ret = "invalidUser"
                        is FirebaseAuthWeakPasswordException -> ret = (task.exception as FirebaseAuthWeakPasswordException).message.toString()
                        is FirebaseAuthRecentLoginRequiredException -> ret = "triggerReAuth"
                    }
                }
            }
        return ret
    }

    override fun updateUserEmail(newEmail: String): Boolean{
        var ret = true
        auth.currentUser!!.verifyBeforeUpdateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    //no exceptions listed in documentation
                    Timber.tag(tag).d(task.exception, "failed to update email")
                    if (task.exception is FirebaseAuthInvalidUserException){
                        ret = false
                    }
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
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ret = true
                    Timber.tag(tag).d("Email sent.")
                }else{
                    //if the email was not a valid user
                    // Don't say anything to protect vs email enumeration attacks
                }
            }
            .await()
        return ret
    }

    /* signs in the user with the email and password
    * on success returns true
    * else false*/
    override suspend fun signInWithEmailPassword(email: String, password: String): Boolean{
        var ret = true
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                Timber.tag(tag).i("completed!")
//                if (task.isSuccessful) {
//                    Timber.tag(tag).i("successful!")
//                    //check if their email is verified
//                    if (auth.currentUser!!.isEmailVerified){
//                        // go to their stories page
//                        Timber.tag(tag).d("email verified!")
//                    }else{
//                        // if their email is unverified
//                        //TODO DOUBLE CHECK IF WE WANTED TO NAV TO EMAIL VERIFY
//                        Timber.tag(tag).d("email unverified")
//                    }
//
//                } else {
//                        // If sign in fails, display a message to the user.
//                        Timber.tag(tag).i("fails: %s", task.exception.toString())
//                        if (task.exception is FirebaseAuthInvalidUserException ||
//                            task.exception is FirebaseAuthInvalidCredentialsException
//                        ){
//                            //email and/or password is incorrect
//                            //not differentiating to protect vs email enumeration attacks
//                            ret = false
//                        }
//                }
//            }
//            .await()
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

                    } else {
//                        // If sign in fails, display a message to the user.
//                        Timber.tag(tag).i("fails: %s", task.exception.toString())
//                        if (task.exception is FirebaseAuthInvalidUserException ||
//                            task.exception is FirebaseAuthInvalidCredentialsException
//                        ){
//                            //email and/or password is incorrect
//                            //not differentiating to protect vs email enumeration attacks
//                            ret = false
//                        }
                    }
                }
                .await()
        }catch (e: Exception){
            // If sign in fails, display a message to the user.
            Timber.tag(tag).i("fails: %s", e.toString())
            if (e is FirebaseAuthInvalidUserException ||
                e is FirebaseAuthInvalidCredentialsException
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
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Timber.tag(tag).d("createUserWithEmail:success")
                    CoroutineScope(Dispatchers.IO).launch {
                        //try and set up their document here, TODO mitigate failure -do again later?
                        ret = createUser(UserEntity(auth.currentUser?.email))

                    }
                } else {
                    // If sign in fails, display a message to the user.
                    val exception = task.exception
                    Timber.tag(tag).w(exception, "createUserWithEmail:failure")
                    ret = when(exception){
                        is FirebaseAuthWeakPasswordException -> exception.message.toString()
                        is FirebaseAuthInvalidCredentialsException -> R.string.malformed_email_message
                        is FirebaseAuthUserCollisionException -> R.string.user_collision_message
                        else -> null
                    }
                }
            }
            .await()
        return ret
    }

    /*creates a new user in Firebase
    * this includes the overall user
    *               AND
    * the story titles document in the stories collection used to ensure unique titles*/
    override suspend fun createUser(user: UserEntity): Boolean{
        var ret = true

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

        }.addOnSuccessListener {

        }.addOnFailureListener{e ->
            ret = false
            Timber.tag(tag).w(e, "Error setting up user")
        }
            .await()

        return ret
    }

    /* deletes user and their data and on success returns "navToSignIn"
    * on failure returns:
    * "invalidUser" if FirebaseAuthInvalidUserException
    * "triggerReAuth" if FirebaseAuthRecentLoginRequiredException*/
    override fun deleteUser(): String{
        val user = auth.currentUser!!
        var ret = "navToSignIn"
        val userUID = user.uid
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUserData(userUID)

                    Timber.tag(tag).d("User account deleted.")
                } else{
                    if (task.exception is FirebaseAuthRecentLoginRequiredException){
                        ret = "triggerReAuth"
                    }else if (task.exception is FirebaseAuthInvalidUserException){
                        ret = "invalidUser"
                    }
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