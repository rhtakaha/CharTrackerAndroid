package com.chartracker.database

import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface ImageDBInterface {
    suspend fun addImage(filename: String, imageURI: Uri): String

    fun deleteImage(filename: String)
}

class ImageDB : ImageDBInterface {
    private val tag = "imageDB"
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    /*****************************************    IMAGE  ******************************************/
    /* function for adding an image into cloud storage
    * input: String filename WITHOUT EXTENSION and local imageUri*/
    override suspend fun addImage(filename: String, imageURI: Uri): String{
        var downloadUrl = ""
        // make a reference to where the file will be
        val storageRef = storage.reference
        val imageRef = storageRef.child("users/${auth.currentUser!!.uid}/images/$filename")

        try{
            downloadUrl = imageRef.putFile(imageURI)
                .continueWithTask { uploadTask ->
                    if (!uploadTask.isSuccessful) {
                        throw uploadTask.exception!!
                    }
                    return@continueWithTask imageRef.downloadUrl
                }
                .await()
                .toString()
        }catch (e: Exception){
            // if it is still an empty string it knows an error occurred
        }

        //TODO probably add associated Toast or something to let users know they are waiting for the upload
        return downloadUrl
    }

    //TODO maybe add exponential backoff to retry this since if this fails we have floating images in storage which is not good
    override fun deleteImage(filename: String){
        val imageRef = storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename")
        try {
            imageRef.delete().addOnSuccessListener {
                // File deleted successfully
                Timber.tag(tag).d("successfully deleted the image")
            }
        }catch (exception: Exception){
            // Uh-oh, an error occurred!
            Timber.tag(tag).d("error deleting the image")
        }
    }
}

class MockImageDB: ImageDBInterface{
    /* mocked add image
    * returns "downloadUrl" if the imageUri (string) is "fileUri"
    * else returns ""*/
    override suspend fun addImage(filename: String, imageURI: Uri): String {
        if (imageURI.toString() == "fileUri"){
            return "downloadUrl"
        }
        return ""
    }

    override fun deleteImage(filename: String) {
        //nothing to be done
    }

}