package com.chartracker.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

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
        try {
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

            }
                .await()
        }catch (exception: Exception){
            ret = false
            Timber.tag(tag).w(exception, "Error creating story")
        }

        return ret
    }

    override suspend fun getStories(): MutableList<StoryEntity>? {
        var stories: MutableList<StoryEntity>? = mutableListOf()

        try {
            //getting every document in the story subcollection
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.id != "titles") {
                            stories?.add(buildStoryFromDocumentSnapshot(document))
                        }
                    }
                    Timber.tag(tag).i("success")

                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).d(exception, "Error getting documents: ")
            stories = null
        }
        return stories
    }

    override suspend fun getStoryId(storyTitle: String): String{
        var story = ""
        try {
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .whereEqualTo("name", storyTitle)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.isEmpty){
                        story = document.documents[0].id
                        Timber.tag(tag).w("Successfully retrieved the document ID")
                    }else{
                        Timber.tag(tag).w("Error: could not find the document ID")
                    }
                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error getting documents: ")
        }

        return story
    }

    /*Document ID to story*/
    override suspend fun getStoryFromId(storyId: String): StoryEntity{
        var story = StoryEntity()
        try {
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

                        Timber.tag(tag).w("Successfully retrieved the story from the given ID ")
                    }else{
                        Timber.tag(tag).w("Error: could not find the story from the given ID ")
                    }
                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error getting story from the given ID: ")
        }

        return story
    }

    /*update the document associated with the given Id with the given StoryEntity*/
    override suspend fun updateStory(storyId: String, story: StoryEntity, currentTitles: List<String>?): Boolean{
        var ret = true
        try {
            if (currentTitles == null){
                // if no name change then no need to update titles doc
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
                    .set(story.toHashMap())
                    .addOnSuccessListener { Timber.tag(tag).d("Story successfully updated!") }
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

                }
                    .await()
            }
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error updating story")
            ret = false
        }

        return ret
    }

    //TODO figure out how best to handle the characters subcollection
    //  1) query all the characters and delete them manually here (probably not recommended)
    //  2) can try the cloud function as in the documentation (no part of the spark plan)
    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    override fun deleteStory(storyId: String, currentTitles: List<String>){
        try {
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
                Timber.tag(tag).d("Story successfully deleted!")
            }
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error deleting story")
        }

    }

    /* function to get all the in use titles*/
    @Suppress("UNCHECKED_CAST")
    override suspend fun getCurrentTitles(): MutableList<String>?{
        var titles: MutableList<String>? = mutableListOf()

        try {
            //getting every document in the story subcollection
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document("titles")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (!task.result.exists()) {
                            CoroutineScope(Dispatchers.IO).launch {
                                db.collection("users")
                                    .document(auth.currentUser!!.uid)
                                    .collection("stories")
                                    .document("titles")
                                    .set(hashMapOf("titles" to listOf<String>()))
                                    .addOnSuccessListener {
                                        titles = mutableListOf()
                                    }
                                    .addOnFailureListener {
                                        titles = null
                                    }
                                    .await()
                            }
                        } else {
                            titles = task.result.get("titles") as MutableList<String>?
                        }
                        Timber.tag(tag).i("success")
                    }
                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).d(exception, "Error getting current titles")
            titles = null
        }
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

class MockStoryDB: StoryDBInterface{
    /* mocked create story
    * returns true if the story title is "title"
    * else returns false*/
    override suspend fun createStory(story: StoryEntity, currentTitles: List<String>): Boolean {
        return story.name.value == "title"
    }

    /*mocked get stories
    this always returns a list of stories*/
    override suspend fun getStories(): MutableList<StoryEntity> {
        return mutableListOf(
            StoryEntity(
                name = "Lord of the Rings",
                genre = "Fantasy",
                author = "JRR Tolkien"
            ),
            StoryEntity(
                name = "Dune",
                genre = "Sci-Fi",
                author = "Frank Herbert"
            ),
            StoryEntity(
                name = "Star Wars",
                genre = "Sci-Fi",
                author = "George Lucas"
            )
        )
    }

    /* mocked get story id
    * returns "id" if the story title is "title"
    * returns "not" if the story title is "xd"
    * return "Paul" if the story title is "Dune"
    * else returns ""*/
    override suspend fun getStoryId(storyTitle: String): String {
        if (storyTitle == "title"){
            return "id"
        }
        if (storyTitle == "xd"){
            return "not"
        }
        if (storyTitle == "Dune"){
            return "Paul"
        }
        return ""
    }

    /* mocked get story from id
    * returns Dune story if story id is "Paul"
    * returns Lord of the Rings if story id is "id"
    * else returns empty story*/
    override suspend fun getStoryFromId(storyId: String): StoryEntity {
        if (storyId == "Paul"){
            return StoryEntity(
                name = "Dune",
                genre = "Sci-Fi",
                author = "Frank Herbert"
            )
        }
        if (storyId == "id"){
            return StoryEntity(name = "Lord of the Rings")
        }
        return StoryEntity()
    }

    /* mocked update story
    * returns true if story id is "id"
    * else returns false*/
    override suspend fun updateStory(
        storyId: String,
        story: StoryEntity,
        currentTitles: List<String>?
    ): Boolean {
        return storyId == "id"
    }

    override fun deleteStory(storyId: String, currentTitles: List<String>) {
        return
    }

    /*mocked get current titles
    this always returns a list of titles (strings)*/
    override suspend fun getCurrentTitles(): MutableList<String> {
        return mutableListOf("Lord of the Rings", "Dune", "Star Wars")
    }
}