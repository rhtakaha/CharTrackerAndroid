package com.chartracker.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

interface StoryDBInterface {
    /*creates a new story in Firebase*/
    suspend fun createStory(story: StoryEntity, currentTitles: List<String>): Boolean
    suspend fun getStories(): MutableList<StoryEntity>?

    suspend fun getStoryId(storyTitle: String): String

    /*Document ID to story*/
    suspend fun getStoryFromId(storyId: String): StoryEntity

    /*update the document associated with the given Id with the given StoryEntity*/
    suspend fun updateStory(storyId: String, story: StoryEntity, currentTitles: List<String>?): Boolean

    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    fun deleteStory(storyId: String, currentTitles: List<String>)

    /* function to get all the in use titles*/
    suspend fun getCurrentTitles(): MutableList<String>?
}

class StoryDB : StoryDBInterface {
    private val tag = "StoryDB"
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /*****************************************    STORY  ******************************************/
    /*creates a new story in Firebase*/
    override suspend fun createStory(story: StoryEntity, currentTitles: List<String>): Boolean{
        var ret = true

        // batched operation so atomic
        db.runBatch {batch ->

            // reference to the new story being created
            val storyRef = db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document()

            //create story
            batch.set(
                storyRef,
                story.toHashMap()
            )

            // created the names document for the characters of this story
            batch.set(
                storyRef
                    .collection("characters")
                    .document("names"),
                hashMapOf("names" to listOf("names"))
            )


            // update titles document by adding the new title
            batch.update(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document("titles"),
                "titles",
                currentTitles
            )

        }.addOnSuccessListener {

        }.addOnFailureListener{e ->
            ret = false
            Log.w(tag, "Error creating story", e)
        }
            .await()

        return ret
    }

    override suspend fun getStories(): MutableList<StoryEntity>? {
        var stories: MutableList<StoryEntity>? = mutableListOf()
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT

        //getting every document in the story subcollection
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .get(source)
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id != "titles") {
                        Log.i(tag, "document: ${document.data}")
                        stories?.add(buildStoryFromDocumentSnapshot(document))
                        Log.i(tag, "${document.id} => ${document.data}")
                    }
                }
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
                stories = null
            }.await()

        Log.i(tag, "returning!")
        return stories
    }

    override suspend fun getStoryId(storyTitle: String): String{
        Log.w(tag, "stared retrieving the story document ID")
        var story = ""
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .whereEqualTo("name", storyTitle)
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

    /*Document ID to story*/
    override suspend fun getStoryFromId(storyId: String): StoryEntity{
        Log.i(tag, "Start getting story from Id")
        var story = StoryEntity()
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    document.data?.let {
                        story = buildStoryFromDocumentSnapshot(document)
                    }

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

    /*update the document associated with the given Id with the given StoryEntity*/
    override suspend fun updateStory(storyId: String, story: StoryEntity, currentTitles: List<String>?): Boolean{
        var ret = true
        if (currentTitles == null){
            // if no name change then no need to update titles doc
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .set(story.toHashMap())
                .addOnSuccessListener { Log.d(tag, "Story successfully updated!") }
                .addOnFailureListener { e ->
                    Log.w(tag, "Error updating story", e)
                    ret = false
                }
                .await()
        }else{
            // if the name changed then need to update titles
            // batched operation so atomic
            db.runBatch {batch ->

                //update story
                batch.set(
                    db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .collection("stories")
                        .document(storyId),
                    story.toHashMap()
                )


                // update titles document by adding the new title
                batch.update(
                    db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .collection("stories")
                        .document("titles"),
                    "titles",
                    currentTitles
                )

            }.addOnSuccessListener {

            }.addOnFailureListener{e ->
                ret = false
                Log.w(tag, "Error updating story and titles", e)
            }
                .await()
        }

        return ret
    }



    //TODO figure out how best to handle the characters subcollection
    //  1) query all the characters and delete them manually here (probably not recommended)
    //  2) can try the cloud function as in the documentation (no part of the spark plan)
    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    override fun deleteStory(storyId: String, currentTitles: List<String>){
        // batched operation so atomic
        db.runBatch {batch ->

            //delete story
            batch.delete(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
            )


            // update titles document by removing the title
            batch.update(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document("titles"),
                "titles",
                currentTitles
            )

        }.addOnSuccessListener {
            Log.d(tag, "Story successfully deleted!")
        }.addOnFailureListener{e ->
            Log.w(tag, "Error deleting story", e)
        }
    }

    /* function to get all the in use titles*/
    @Suppress("UNCHECKED_CAST")
    override suspend fun getCurrentTitles(): MutableList<String>?{
        var titles: MutableList<String>? = mutableListOf()

        //getting every document in the story subcollection
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document("titles")
            .get()
            .addOnSuccessListener { result ->
                titles = result.get("titles") as MutableList<String>?
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
                titles = null
            }
            .await()
        return titles
    }

    private fun buildStoryFromDocumentSnapshot(document: DocumentSnapshot): StoryEntity{
        return StoryEntity(
            name = document.data!!["name"].toString(),
            genre = document.data!!["genre"].toString(),
            type = document.data!!["type"].toString(),
            author= document.data!!["author"].toString(),
            imageFilename = document.data!!["imageFilename"]?.toString(),
            imagePublicUrl = document.data!!["imagePublicUrl"]?.toString()
        )
    }
}