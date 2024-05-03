package com.chartracker.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface StoryDBInterface {
    /*creates a new story in Firebase*/
    suspend fun createStory(
        story: StoryEntity,
        currentTitles: List<String>,
        navToStories: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit)
    suspend fun getStories(
        stories: MutableStateFlow<MutableList<StoryEntity>>,
        failedGetStories: MutableState<Boolean>)

    suspend fun getStoryId(storyTitle: String): String

    /*Document ID to story*/
    suspend fun getStoryFromId(
        storyId: String,
        story: MutableState<StoryEntity>,
        originalFilename: MutableState<String?> = mutableStateOf(""),
        originalStoryTitle: MutableState<String> = mutableStateOf(""))

    /*update the document associated with the given Id with the given StoryEntity*/
    suspend fun updateStory(
        storyId: String,
        story: StoryEntity,
        currentTitles: List<String>,
        updatedTitle: Boolean,
        navToStories: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit)

    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    fun deleteStory(storyId: String, currentTitles: List<String>)

    /* function to get all the in use titles*/
    suspend fun getCurrentTitles(titles: MutableList<String>, error: MutableState<Boolean>)
}

class StoryDB : StoryDBInterface {
    private val tag = "StoryDB"
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /*****************************************    STORY  ******************************************/
    /*creates a new story in Firebase*/
    override suspend fun createStory(
        story: StoryEntity,
        currentTitles: List<String>,
        navToStories: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit){

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
                hashMapOf("names" to listOf<String>())
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
            .addOnSuccessListener {
                navToStories.value = true
            }
            .addOnFailureListener {exception ->
                Timber.tag(tag).w(exception, "Error creating story")
                if (hasImage){
                    // only delete the image if it uploaded one
                    deleteImage()
                }
                uploadError.value = true
            }
    }

    override suspend fun getStories(
        stories: MutableStateFlow<MutableList<StoryEntity>>,
        failedGetStories: MutableState<Boolean>){
        stories.value.clear()
        //getting every document in the story subcollection
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id != "titles") {
                        stories.value.add(buildStoryFromDocumentSnapshot(document))
                    }
                }
                Timber.tag(tag).i("got the stories")

            }
            .addOnFailureListener { exception ->
                Timber.tag(tag).d(exception, "Error getting documents: ")
                failedGetStories.value = true
            }
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
    override suspend fun getStoryFromId(
        storyId: String,
        story: MutableState<StoryEntity>,
        originalFilename: MutableState<String?>,
        originalStoryTitle: MutableState<String>){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    document.data?.let {
                        story.value = buildStoryFromDocumentSnapshot(document)
                        originalStoryTitle.value = story.value.name.value
                        originalFilename.value = story.value.imageFilename.value
                    }

                    Timber.tag(tag).w("Successfully retrieved the story from the given ID ")
                }else{
                    Timber.tag(tag).w("Error: could not find the story from the given ID ")
                }
            }
            .addOnFailureListener {exception ->
                Timber.tag(tag).w(exception, "Error getting story from the given ID: ")
            }
    }

    /*update the document associated with the given Id with the given StoryEntity*/
    override suspend fun updateStory(
        storyId: String,
        story: StoryEntity,
        currentTitles: List<String>,
        updatedTitle: Boolean,
        navToStories: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit){

        if (!updatedTitle){
            // if no name change then no need to update titles doc
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .set(story.toHashMap())
                .addOnSuccessListener {
                    Timber.tag(tag).d("Story successfully updated!")
                    navToStories.value = true
                }
                .addOnFailureListener {exception ->
                    Timber.tag(tag).w(exception, "Error updating story")
                    if (hasImage){
                        // only delete the image if it uploaded one
                        deleteImage()
                    }
                    uploadError.value = true
                }
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

            }
                .addOnSuccessListener {
                    navToStories.value = true
                }
                .addOnFailureListener {exception ->
                    Timber.tag(tag).w(exception, "Error updating story")
                    if (hasImage){
                        // only delete the image if it uploaded one
                        deleteImage()
                    }
                    uploadError.value = true
                }
        }
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
    override suspend fun getCurrentTitles(titles: MutableList<String>, error: MutableState<Boolean>){

        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document("titles")
            .get()
            .addOnSuccessListener {docSnap ->
                if (!docSnap.exists()) {
                    // if we couldn't find it make it
                    CoroutineScope(Dispatchers.IO).launch {
                        db.collection("users")
                            .document(auth.currentUser!!.uid)
                            .collection("stories")
                            .document("titles")
                            .set(hashMapOf("titles" to listOf<String>()))
                            .addOnSuccessListener {
                                // default is empty list so nothing to do
                            }
                            .addOnFailureListener {
                                error.value = true
                            }
                    }
                } else {
                    titles.addAll(docSnap.get("titles") as MutableList<String>)
                }
                Timber.tag(tag).i("success")
            }
            .addOnFailureListener {exception ->
                Timber.tag(tag).d(exception, "Error getting current titles")
                error.value = true
            }
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
    * sets navToStories state to true if the story title is "title"
    * else sets uploadError state to true and calls the deleteImage function*/
    override suspend fun createStory(
        story: StoryEntity,
        currentTitles: List<String>,
        navToStories: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit) {
        if (story.name.value == "title"){
            navToStories.value = true
        }else{
            if (hasImage){
                deleteImage()
            }
            uploadError.value = true
        }
    }

    /*mocked get stories
    this always sets the state to a list of stories*/
    override  suspend fun getStories(
        stories: MutableStateFlow<MutableList<StoryEntity>>,
        failedGetStories: MutableState<Boolean>) {
        stories.value.addAll(
            mutableListOf(
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
        )
    }

    /* mocked get story id
    * returns "id" if the story title is "title"
    * returns "not" if the story title is "xd"
    * return "Paul" if the story title is "Dune"
    * else returns ""*/
    override suspend fun getStoryId(storyTitle: String): String {
        if (storyTitle == "Lord of the Rings"){
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
    * sets story state to Dune story if story id is "Paul"
    * sets story state to Lord of the Rings if story id is "id"
    * */
    override suspend fun getStoryFromId(
        storyId: String,
        story: MutableState<StoryEntity>,
        originalFilename: MutableState<String?>,
        originalStoryTitle: MutableState<String>) {
        if (storyId == "Paul"){
            story.value = StoryEntity(
                name = "Dune",
                genre = "Sci-Fi",
                author = "Frank Herbert"
            )
        }
        if (storyId == "id"){
            story.value = StoryEntity(name = "Lord of the Rings")
        }
    }

    /* mocked update story
    * returns true if story id is "id"
    * else returns false*/
    override suspend fun updateStory(
        storyId: String,
        story: StoryEntity,
        currentTitles: List<String>,
        updatedTitle: Boolean,
        navToStories: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit){
        if (storyId == "id"){
            navToStories.value = true
        }else{
            if (hasImage){
                deleteImage()
            }
            uploadError.value = true
        }
    }

    override fun deleteStory(storyId: String, currentTitles: List<String>) {
        return
    }

    /*mocked get current titles
    this always returns a list of titles (strings)*/
    override suspend fun getCurrentTitles(titles: MutableList<String>, error: MutableState<Boolean>) {
        titles.addAll(mutableListOf("Lord of the Rings", "Dune", "Star Wars"))
    }
}