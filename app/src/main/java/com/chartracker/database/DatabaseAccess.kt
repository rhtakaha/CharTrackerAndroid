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

    suspend fun getDocId(storyTitle: String): String{
        var story: String = ""
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .whereEqualTo("title", storyTitle)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty){
                    story = document.documents[0].id
                    Log.w(TAG, "Successfully retrieved the document ID")
                }else{
                    Log.w(TAG, "Error: could not find the document ID")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
            .await()
        return story
    }

    /*update the document associated with the given Id with the given StoriesEntity*/
    suspend fun updateStory(storyID: String, story: StoriesEntity){
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .document(storyID)
            .set(story)
            .addOnSuccessListener { Log.d(TAG, "Story successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating story", e) }
    }

    /*Document ID to story*/
    suspend fun getStoryFromId(storyId: String): StoriesEntity{
        var story: StoriesEntity = StoriesEntity()
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    story = document.toObject()!!
                    Log.w(TAG, "Successfully retrieved the story from the given ID ")
                }else{
                    Log.w(TAG, "Error: could not find the story from the given ID ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting story from the given ID: ", exception)
            }
            .await()
        return story
    }


    suspend fun getStory(storyTitle: String): StoriesEntity{
        var story: StoriesEntity = StoriesEntity()
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .whereEqualTo("title", storyTitle)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty){
                    story = document.documents[0].toObject()!!
                    Log.w(TAG, "Successfully retrieved the document ")
                }else{
                    Log.w(TAG, "Error: could not find the document ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
            .await()
        return story
    }

    /*given the document ID of the story return the list of characters*/
    suspend fun getCharacters(storyId: String): MutableList<CharacterEntity>{
        val characters = mutableListOf<CharacterEntity>()
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT

        //getting every document in the characters subcollection of that particular story
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .get(source)
            .addOnSuccessListener { result ->
                for (document in result) {
                    characters.add(document.toObject<CharacterEntity>())
                    Log.i(TAG, "${document.id} => ${document.data}")
                }
                Log.i(TAG, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
            .await()

        Log.i(TAG, "returning!")
        return characters
    }

    /*creates a new story in Firebase*/
    suspend fun createStory(story: StoriesEntity){
        db.collection("users").document("1oWdT6v9mMl0oIMb0Sj7").collection("stories")
            .add(story)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }


    //TODO Eventually going to probably have a function running in the background
    // which updates the offline cache and then when the user uses the app it pulls
    // from offline unless a change was made
    suspend fun getStories(): MutableList<StoriesEntity> {
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