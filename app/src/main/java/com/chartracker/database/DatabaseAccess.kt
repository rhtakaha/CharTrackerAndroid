package com.chartracker.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await




class DatabaseAccess {
    private val tag = "dbAccess"
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    /*creates a new user in Firebase*/
    suspend fun createUser(user: UserEntity){
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

    suspend fun updateCharacter(storyId: String, charId: String, character: CharacterEntity){
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .document(charId)
            .set(character)
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
                    character = document.toObject<CharacterEntity>()!!
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
                    character = documents.documents[0].toObject<CharacterEntity>()!!
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
    suspend fun createCharacter(storyId: String, character: CharacterEntity){
        db.collection("users")
            .document(auth.currentUser!!.uid)
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
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .delete()
            .addOnSuccessListener { Log.d(tag, "Story successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting story", e) }
    }

    suspend fun getStoryId(storyTitle: String): String{
        var story: String = ""
        db.collection("users")
            .document(auth.currentUser!!.uid)
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
            .document(auth.currentUser!!.uid)
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
            .document(auth.currentUser!!.uid)
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
            .document(auth.currentUser!!.uid)
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
            .document(auth.currentUser!!.uid)
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
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
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
            .document(auth.currentUser!!.uid)
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
}