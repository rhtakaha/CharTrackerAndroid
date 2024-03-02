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
import com.google.firebase.storage.StorageReference
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

    suspend fun addImageDownloadUrlToCharacter(character: CharacterEntity, filename: String){
        storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename").downloadUrl.addOnSuccessListener {url ->
            character.imagePublicUrl.value = url.toString()
            Log.i(tag, "got the public url for the image: $url")
        }.addOnFailureListener {
            // Handle any errors
            Log.i(tag, "failed to get public url for image: $it")
        }.await()
    }

    suspend fun addImageDownloadUrlToStory(story: StoryEntity, filename: String): Boolean{
        var ret = true
        storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename").downloadUrl.addOnSuccessListener {url ->
            story.imagePublicUrl.value = url.toString()
            Log.i(tag, "got the public url for the image: $url")
        }.addOnFailureListener {
            // Handle any errors
            Log.i(tag, "failed to get public url for image: $it")
            ret = false
        }.await()
        return ret
    }

    fun getImageRef(filename: String): StorageReference{
        return storage.reference.child("users/${auth.currentUser!!.uid}/images/$filename")
    }

    /* function for adding an image into cloud storage
    * input: String filename WITHOUT EXTENSION and local imageUri*/
    suspend fun addImage(filename: String, imageURI: Uri): Boolean{
        var ret = true
        // make a reference to where the file will be
        val storageRef = storage.reference
        val imageRef = storageRef.child("users/${auth.currentUser!!.uid}/images/$filename")

//        val file = Uri.fromFile(File(imageURI))
        val uploadTask = imageRef.putFile(imageURI)

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Log.d(tag, "image upload failed")
            ret = false
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
            Log.d(tag, "image upload succeeded!")
        }.await()
        //TODO probably add associated Toast or something to let users know they are waiting for the upload
        return ret
    }

    /*creates a new user in Firebase*/
    fun createUser(user: UserEntity){
        auth.currentUser?.let {
            db.collection("users")
                .document(it.uid)
                .set(user)
                .addOnSuccessListener { Log.d(tag, "Successfully made the user document") }
                .addOnFailureListener { e -> Log.w(tag, "Error writing user document", e) }
        }
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

    fun updateCharacter(storyId: String, charId: String, character: CharacterEntity){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .document(charId)
            .set(character.toHashMap())
            .addOnSuccessListener { Log.d(tag, "Character successfully updated!") }
            .addOnFailureListener { e -> Log.w(tag, "Error updating character", e) }
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

    suspend fun getCharacterFromId(storyId: String, charId: String): CharacterEntity{
        var character = CharacterEntity()
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .document(charId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null){
                    character = buildCharacterFromDocumentSnapshot(document)
//                    character = document.toObject<CharacterEntity>()!!
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
    fun createCharacter(storyId: String, character: CharacterEntity){
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
            }
    }

    //TODO figure out how best to handle the characters subcollection
    //  1) query all the characters and delete them manually here (probably not recommended)
    //  2) can try the cloud function as in the documentation (no part of the spark plan)
    /*more complex because Story can (will) have a subcollection and cannot easily delete them together
    * it isn't recommended to do the deletion on mobile clients either so just going to delete the story doc which should work for now*/
    fun deleteStory(storyId: String){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .delete()
            .addOnSuccessListener { Log.d(tag, "Story successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting story", e) }
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
    suspend fun updateStory(storyId: String, story: StoryEntity): Boolean{
        var ret = true
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


    suspend fun getStory(storyTitle: String): StoryEntity{
        var story = StoryEntity()
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .whereEqualTo("name", storyTitle)
            .get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty){
                    story = buildStoryFromDocumentSnapshot(document.documents[0])
//                    story = document.documents[0].toObject()!!
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
        Log.i(tag, "Start get characters")
        val characters = mutableListOf<CharacterEntity>()
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
                    characters.add(buildCharacterFromDocumentSnapshot(document))
//                    characters.add(document.toObject<CharacterEntity>())
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
    suspend fun createStory(story: StoryEntity): Boolean{
        var ret = true
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .add(story.toHashMap())
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
                    Log.i(tag, "document: ${document.data}")
                    stories?.add(buildStoryFromDocumentSnapshot(document))
                    Log.i(tag, "${document.id} => ${document.data}")
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