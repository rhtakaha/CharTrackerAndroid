package com.chartracker.database

import android.util.Log
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


private const val TAG = "dbAccess"

class DatabaseAccess {
    private val db = Firebase.firestore


    //TODO Eventually going to probably have a function running in the background
    // which updates the offline cache and then when the user uses the app it pulls
    // from offline unless a change was made
    suspend fun getStory(): MutableList<StoriesEntity> {
            val stories = mutableListOf<StoriesEntity>()
            // Source can be CACHE, SERVER, or DEFAULT.
            val source = Source.DEFAULT

            //getting every document in the story subcollection
            db.collection("users")
                .document("1oWdT6v9mMl0oIMb0Sj7")
                .collection("stories")
                .get(source)
                .addOnSuccessListener { result ->
                    for (document in result) {
                        stories.add(document.toObject<StoriesEntity>())
                        Log.i(TAG, "${document.id} => ${document.data}")
                    }
                    Log.i(TAG, "success")

                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }.await()

        Log.i(TAG, "returning!")
        return stories
    }

    //WORKS
    suspend fun getUser(){
        Log.i(TAG, "trying to get the USER")
        val docRef = db.collection("users").document("1oWdT6v9mMl0oIMb0Sj7")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}