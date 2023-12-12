package com.chartracker.database

import android.util.Log
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await




class DatabaseAccess {
    private val tag = "dbAccess"
    private val db = Firebase.firestore

    /* creates a character document in the given story*/
    suspend fun createCharacter(storyId: String, character: CharacterEntity){
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .add(character)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
            }
    }

    //TODO figure out how best to handle the characters subcollection
    //  1) query all the characters and delete them manually here (probably not recommended)
    //  2) can try the cloud function as in the documentation (no part of the spark plan)
    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    suspend fun deleteStory(storyId: String){
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .document(storyId)
            .delete()
            .addOnSuccessListener { Log.d(tag, "Story successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting story", e) }
    }

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
                    Log.w(tag, "Successfully retrieved the document ID")
                }else{
                    Log.w(tag, "Error: could not find the document ID")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting documents: ", exception)
            }
            .await()
        return story
    }

    /*update the document associated with the given Id with the given StoriesEntity*/
    suspend fun updateStory(storyId: String, story: StoriesEntity){
        db.collection("users")
            .document("1oWdT6v9mMl0oIMb0Sj7")
            .collection("stories")
            .document(storyId)
            .set(story)
            .addOnSuccessListener { Log.d(tag, "Story successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error updating story", e) }
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
                    Log.w(tag, "Successfully retrieved the story from the given ID ")
                }else{
                    Log.w(tag, "Error: could not find the story from the given ID ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting story from the given ID: ", exception)
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
                    Log.w(tag, "Successfully retrieved the document ")
                }else{
                    Log.w(tag, "Error: could not find the document ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting documents: ", exception)
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
                    Log.i(tag, "${document.id} => ${document.data}")
                }
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
            }
            .await()

        Log.i(tag, "returning!")
        return characters
    }

    /*creates a new story in Firebase*/
    suspend fun createStory(story: StoriesEntity){
        db.collection("users").document("1oWdT6v9mMl0oIMb0Sj7").collection("stories")
            .add(story)
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
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
                    Log.i(tag, "${document.id} => ${document.data}")
                }
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
            }.await()

        Log.i(tag, "returning!")
        return stories
    }

    //WORKS
    suspend fun getUser(){
        Log.i(tag, "trying to get the USER")
        val docRef = db.collection("users").document("1oWdT6v9mMl0oIMb0Sj7")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(tag, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(tag, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(tag, "get failed with ", exception)
            }
    }
}