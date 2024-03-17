package com.chartracker.database

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


class DatabaseAccess {
    private val tag = "dbAccess"
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    fun enableEmulatorTesting(){
        Log.i(tag, "ENABLING EMULATORS")
        db.useEmulator("10.0.2.2", 8080)
        auth.useEmulator("10.0.2.2", 9099)
        storage.useEmulator("10.0.2.2", 9199)


        db.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = false
        }
    }

    //TODO maybe add exponential backoff to retry this since if this fails we have floating images in storage which is not good
    fun deleteImage(filename: String){
        val imageRef = storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename")
        imageRef.delete().addOnSuccessListener {
            // File deleted successfully
            Log.d(tag, "successfully deleted the image")
        }.addOnFailureListener {
            // Uh-oh, an error occurred!
            Log.d(tag, "error deleting the image")
        }
    }

//    fun getImageRef(filename: String): StorageReference{
//        return storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename")
//    }

    /* function for adding an image into cloud storage
    * input: String filename WITHOUT EXTENSION and local imageUri*/
    suspend fun addImage(filename: String, imageURI: Uri): String{
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

    /*creates a new user in Firebase
    * this includes the overall user
    *               AND
    * the story titles document in the stories collection used to ensure unique titles*/
    suspend fun createUser(user: UserEntity): Boolean{
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
            Log.w(tag, "Error setting up user", e)
        }
            .await()

//        db.collection("users")
//            .document(auth.currentUser!!.uid)
//            .set(user)
//            .addOnSuccessListener {
//                Log.d(tag, "Successfully made the user document")
//            }
//            .addOnFailureListener { e ->
//                ret = false
//                Log.w(tag, "Error writing user document", e)
//            }.await()

        return ret
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getCurrentTitles(): MutableList<String>?{
        var titles: MutableList<String>? = mutableListOf()

        //getting every document in the story subcollection
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .get()
            .addOnSuccessListener { result ->
                val titlesDoc = result.documents.filter { documentSnapshot -> documentSnapshot.id == "titles" }[0]
                titles = titlesDoc.get("titles") as MutableList<String>?
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
                titles = null
            }
            .await()
        return titles
    }


    /* first queries for characters with a relationship with the to be deleted character
    *   modifies those characters to remove the to be deleted character AND deletes the character ATOMICALLY*/
    suspend fun deleteCharacter(storyId: String, charId: String, charName: String){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .where(Filter.or(
                Filter.arrayContains("allies", charName),
                Filter.arrayContains("enemies", charName),
                Filter.arrayContains("neutral", charName)
            ))
            .get()
            .addOnSuccessListener { documents ->
                db.runBatch { batch ->
                    //query for and update all characters that have this character as an ally/enemy/neutral
                    for( document in documents){
                        // remove the deleted character from the allies and update
                        document.data["allies"]
                        var a = document.data["allies"]
                        if (a is List<*>) {
                            a = a.filter { it != charName }
                        }
                        //only bother with the operation if it does something
                        if (a != document.data["allies"]){
                            batch.update(document.reference, "allies", a)
                        }


                        // remove the deleted character from the enemies and update
                        var e = document.data["enemies"]
                        if (e is List<*>) {
                            e = e.filter { it != charName }
                        }
                        //only bother with the operation if it does something
                        if (e != document.data["enemies"]) {
                            batch.update(document.reference, "enemies", e)
                        }

                        // remove the deleted character from the neutral and update
                        var n = document.data["neutral"]
                        if (n is List<*>) {
                            n = n.filter { it != charName }
                        }
                        //only bother with the operation if it does something
                        if (n != document.data["neutral"]){
                            batch.update(document.reference, "neutral", n)
                        }
                    }

                    // delete this character
                    batch.delete(db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .collection("stories")
                        .document(storyId)
                        .collection("characters")
                        .document(charId))

                }.addOnCompleteListener { Log.d(tag, "batch success!") }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error querying related characters: ", exception)
            }
            .await()
    }

    suspend fun updateCharacter(storyId: String, charId: String, character: CharacterEntity): Boolean{
        var ret = true
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .document(charId)
            .set(character.toHashMap())
            .addOnSuccessListener { Log.d(tag, "Character successfully updated!") }
            .addOnFailureListener { e ->
                ret = false
                Log.w(tag, "Error updating character", e)
            }
            .await()
        return ret
    }

    suspend fun getCharacterId(storyId: String, charName: String): String{
        var character = ""
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .whereEqualTo("name", charName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents[0] != null){
                    character = documents.documents[0].id
                    Log.w(tag, "Successfully retrieved the character ")
                }else{
                    Log.w(tag, "Error: could not find the character ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting character: ", exception)
            }
            .await()
        return character
    }

    @Suppress("UNCHECKED_CAST")
    private fun buildCharacterFromDocumentSnapshot(document: DocumentSnapshot): CharacterEntity{
        return CharacterEntity(
            name = document.data!!["name"].toString(),
            aliases = document.data!!["aliases"].toString(),
            titles = document.data!!["titles"].toString(),
            age = document.data!!["age"].toString(),
            home = document.data!!["home"].toString(),
            gender = document.data!!["gender"].toString(),
            race = document.data!!["race"].toString(),
            livingOrDead = document.data!!["livingOrDead"].toString(),
            occupation = document.data!!["occupation"].toString(),
            weapons = document.data!!["weapons"].toString(),
            toolsEquipment = document.data!!["toolsEquipment"].toString(),
            bio = document.data!!["bio"].toString(),
            faction = document.data!!["faction"].toString(),
            allies = document.data!!["allies"] as List<String>? ,
            enemies = document.data!!["enemies"] as List<String>?,
            neutral = document.data!!["neutral"] as List<String>?,
            imageFilename = document.data!!["imageFilename"]?.toString(),
            imagePublicUrl = document.data!!["imagePublicUrl"]?.toString()
        )
    }

    fun buildStoryFromDocumentSnapshot(document: DocumentSnapshot): StoryEntity{
        return StoryEntity(
                name = document.data!!["name"].toString(),
                genre = document.data!!["genre"].toString(),
                type = document.data!!["type"].toString(),
                author= document.data!!["author"].toString(),
                imageFilename = document.data!!["imageFilename"]?.toString(),
                imagePublicUrl = document.data!!["imagePublicUrl"]?.toString()
            )
    }

//    suspend fun getCharacterFromId(storyId: String, charId: String): CharacterEntity{
//        var character = CharacterEntity()
//        db.collection("users")
//            .document(auth.currentUser!!.uid)
//            .collection("stories")
//            .document(storyId)
//            .collection("characters")
//            .document(charId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document != null){
//                    character = buildCharacterFromDocumentSnapshot(document)
////                    character = document.toObject<CharacterEntity>()!!
//                    Log.w(tag, "Successfully retrieved the character ")
//                }else{
//                    Log.w(tag, "Error: could not find the character ")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(tag, "Error getting character: ", exception)
//            }
//            .await()
//        return character
//    }

    suspend fun getCharacter(storyId: String, charName: String): CharacterEntity{
        Log.i(tag, "getting character with charName: $charName")
        var character = CharacterEntity()
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .whereEqualTo("name", charName)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.documents[0] != null){
                    character = buildCharacterFromDocumentSnapshot(documents.documents[0])
//                    character = documents.documents[0].toObject<CharacterEntity>()!!
                    Log.w(tag, "Successfully retrieved the character ")
                }else{
                    Log.w(tag, "Error: could not find the character ")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(tag, "Error getting character: ", exception)
            }
            .await()
        return character
    }

    /* creates a character document in the given story*/
    suspend fun createCharacter(storyId: String, character: CharacterEntity): Boolean{
        var ret  = true
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .add(character.toHashMap())
            .addOnSuccessListener { documentReference ->
                Log.d(tag, "DocumentSnapshot written with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "Error adding document", e)
                ret = false
            }
            .await()
        return ret
    }

    //TODO figure out how best to handle the characters subcollection
    //  1) query all the characters and delete them manually here (probably not recommended)
    //  2) can try the cloud function as in the documentation (no part of the spark plan)
    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    fun deleteStory(storyId: String, currentTitles: List<String>){
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
//        db.collection("users")
//            .document(auth.currentUser!!.uid)
//            .collection("stories")
//            .document(storyId)
//            .delete()
//            .addOnSuccessListener { Log.d(tag, "Story successfully deleted!") }
//            .addOnFailureListener { e -> Log.w(tag, "Error deleting story", e) }
    }

    //TODO figure out how best to handle the stories and characters subcollection
    fun deleteUserData(userUID: String){
        db.collection("users")
            .document(userUID)
            .delete()
            .addOnSuccessListener { Log.d(tag, "User Data successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting user Data", e) }
    }

    suspend fun getStoryId(storyTitle: String): String{
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

    /*update the document associated with the given Id with the given StoryEntity*/
    suspend fun updateStory(storyId: String, story: StoryEntity, currentTitles: List<String>?): Boolean{
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

    /*Document ID to story*/
    suspend fun getStoryFromId(storyId: String): StoryEntity{
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

//                    story = document.toObject()!!
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


//    suspend fun getStory(storyTitle: String): StoryEntity{
//        var story = StoryEntity()
//        db.collection("users")
//            .document(auth.currentUser!!.uid)
//            .collection("stories")
//            .whereEqualTo("name", storyTitle)
//            .get()
//            .addOnSuccessListener { document ->
//                if (!document.isEmpty){
//                    story = buildStoryFromDocumentSnapshot(document.documents[0])
////                    story = document.documents[0].toObject()!!
//                    Log.w(tag, "Successfully retrieved the document ")
//                }else{
//                    Log.w(tag, "Error: could not find the document ")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(tag, "Error getting documents: ", exception)
//            }
//            .await()
//        return story
//    }

    /*given the document ID of the story return the list of characters*/
    suspend fun getCharacters(storyId: String): MutableList<CharacterEntity>?{
        Log.i(tag, "Start get characters")
        var characters: MutableList<CharacterEntity>? = mutableListOf()
        // Source can be CACHE, SERVER, or DEFAULT.
        val source = Source.DEFAULT

        //getting every document in the characters subcollection of that particular story
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .get(source)
            .addOnSuccessListener { result ->
                for (document in result) {
                    characters?.add(buildCharacterFromDocumentSnapshot(document))
//                    characters.add(document.toObject<CharacterEntity>())
                    Log.i(tag, "${document.id} => ${document.data}")
                }
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
                characters = null
            }
            .await()

        Log.i(tag, "returning!")
        return characters
    }

    /*creates a new story in Firebase*/
    suspend fun createStory(story: StoryEntity, currentTitles: List<String>): Boolean{
        var ret = true

        // batched operation so atomic
        db.runBatch {batch ->

            //create story
            batch.set(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(),
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
            Log.w(tag, "Error creating story", e)
        }
            .await()

        return ret
//        var ret = true
//        db.collection("users")
//            .document(auth.currentUser!!.uid)
//            .collection("stories")
//            .add(story.toHashMap())
//            .addOnSuccessListener { documentReference ->
//                Log.d(tag, "DocumentSnapshot written with ID: ${documentReference.id}")
//            }
//            .addOnFailureListener { e ->
//                Log.w(tag, "Error adding document", e)
//                ret = false
//            }
//            .await()
//        return ret
    }

    suspend fun getStories(): MutableList<StoryEntity>? {
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
}