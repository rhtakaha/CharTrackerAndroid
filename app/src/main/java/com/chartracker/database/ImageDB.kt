package com.chartracker.database

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.core.net.toFile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface ImageDBInterface {
    suspend fun addImage(
        entity: DatabaseEntity,
        imageURI: Uri,
        uploadError: MutableState<Boolean>,
        originalFilename: String? = null
    )

    fun deleteImage(filename: String)
}

class ImageDB : ImageDBInterface {
    private val tag = "imageDB"
    private val auth = Firebase.auth
    private val storage = Firebase.storage
    private val db = Firebase.firestore
    /*****************************************    IMAGE  ******************************************/
    /* function for adding an image into cloud storage
    * input: DatabaseEntity, local imageUri, uploadError state, and (optional) original filename
    * */
    override suspend fun addImage(
        entity: DatabaseEntity,
        imageURI: Uri,
        uploadError: MutableState<Boolean>,
        originalFilename: String?
        ){
        if (!logImage("users/${auth.currentUser!!.uid}/images/${entity.imageFilename}")){
            // if we fail to log we stop here
            uploadError.value = true
        }

        // make a reference to where the file will be
        val storageRef = storage.reference
        val imageRef = storageRef.child("users/${auth.currentUser!!.uid}/images/${entity.imageFilename}")

        try {
            entity.imagePublicUrl.value = imageRef.putFile(imageURI)
                .continueWithTask { uploadTask ->
                    if (!uploadTask.isSuccessful) {
                        throw uploadTask.exception!!
                    }
                    return@continueWithTask imageRef.downloadUrl
                }
                .await()
                .toString()
        }catch (exception: Exception){
            uploadError.value = true
            throw exception
        }


        // cleaning up the on device cache
        val file = imageURI.toFile()
        if (file.exists() && file.isFile) {
            file.delete()
        }
    }

    /* add this filename to the list of every file we are storing
            (for data maintenance purposes)
            Done BEFORE upload
            returns true on success and false on failure*/
    private suspend fun logImage(filename: String): Boolean{
        try {
            db.collection("all")
                .document("imageFiles")
                .update("imageFilenames", FieldValue.arrayUnion(filename))
                .await()
        }catch (e: Exception){
            Timber.tag(tag).i("failed to log file with exception: ${e.message}")
            return false
        }
        return true
    }

    /* remove this filename from the list of every file we are storing
            (for data maintenance purposes)
            Done AFTER delete
            no returns since only called when image is gone
            so either no image and no log if this works
            or no image and a log if this fails
            both are permissible*/
    private fun unLogImage(filename: String){
        try {
            db.collection("all")
                .document("imageFiles")
                .update("imageFilenames", FieldValue.arrayRemove(filename))
        }catch (e: Exception){
            Timber.tag(tag).i("failed to unlog file with exception: ${e.message}")
        }
    }

    //TODO maybe add exponential backoff to retry this since if this fails we have floating images in storage which is not good
    override fun deleteImage(filename: String){
        val imageRef = storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename")
        try {
            imageRef.delete().addOnSuccessListener {
                // File deleted successfully
                Timber.tag(tag).d("successfully deleted the image")
                unLogImage("users/${auth.currentUser!!.uid}/images/$filename")
            }
        }catch (exception: Exception){
            // Uh-oh, an error occurred!
            Timber.tag(tag).d("error deleting the image")
        }
    }
}

class MockImageDB: ImageDBInterface{
    /* mocked add image
    * sets entity imagePublicUrl state to "downloadUrl" if the imageUri (string) is "fileUri"
    * else sets entity imagePublicUrl state to ""*/
    override suspend fun addImage(
        entity: DatabaseEntity,
        imageURI: Uri,
        uploadError: MutableState<Boolean>,
        originalFilename: String?
    ) {
        if (imageURI.toString() == "fileUri"){
            entity.imagePublicUrl.value = "downloadUrl"
        }else{
            uploadError.value = true
            throw Exception("bad image :(")
        }
    }

    override fun deleteImage(filename: String) {
        //nothing to be done
    }

}