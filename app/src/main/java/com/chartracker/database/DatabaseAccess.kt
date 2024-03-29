package com.chartracker.database

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
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

    /*****************************************    USER  *******************************************/
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

        return ret
    }

    //TODO figure out how best to handle the stories and characters subcollection
    fun deleteUserData(userUID: String){
        db.collection("users")
            .document(userUID)
            .delete()
            .addOnSuccessListener { Log.d(tag, "User Data successfully deleted!") }
            .addOnFailureListener { e -> Log.w(tag, "Error deleting user Data", e) }
    }

    /*************************************    CHARACTER  ******************************************/

    /* creates a character document in the given story*/
    suspend fun createCharacter(storyId: String, character: CharacterEntity, currentNames: List<String>): Boolean{
        var ret = true

        // batched operation so atomic
        db.runBatch {batch ->

            //create character
            batch.set(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
                    .collection("characters")
                    .document(),
                character.toHashMap()
            )


            // update names document by adding the new name
            batch.update(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
                    .collection("characters")
                    .document("names"),
                "names",
                currentNames
            )

        }.addOnSuccessListener {

        }.addOnFailureListener{e ->
            ret = false
            Log.w(tag, "Error creating character", e)
        }
            .await()

        return ret
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
                    if (document.id != "names") {
                        characters?.add(buildCharacterFromDocumentSnapshot(document))
                        Log.i(tag, "${document.id} => ${document.data}")
                    }

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

    suspend fun updateCharacter(storyId: String, charId: String, updatedCharacter: CharacterEntity, currentNames: List<String>?): Boolean{
        var ret = true
        if (currentNames == null){
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .collection("characters")
                .document(charId)
                .set(updatedCharacter.toHashMap())
                .addOnSuccessListener { Log.d(tag, "Character successfully updated!") }
                .addOnFailureListener { e ->
                    ret = false
                    Log.w(tag, "Error updating character", e)
                }
                .await()
        }else{
            // if the name changed then need to update names
            // batched operation so atomic
            db.runBatch {batch ->

                //update character
                batch.set(
                    db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .collection("stories")
                        .document(storyId)
                        .collection("characters")
                        .document(charId),
                    updatedCharacter.toHashMap()
                )


                // update names document by adding the new name
                batch.update(
                    db.collection("users")
                        .document(auth.currentUser!!.uid)
                        .collection("stories")
                        .document(storyId)
                        .collection("characters")
                        .document("names"),
                    "names",
                    currentNames
                )

            }.addOnSuccessListener {

            }.addOnFailureListener{e ->
                ret = false
                Log.w(tag, "Error updating character and names", e)
            }
                .await()
        }

        return ret
    }

    /* first queries for characters with a relationship with the to be deleted character
    *   modifies those characters to remove the to be deleted character AND deletes the character ATOMICALLY*/
    /* rather than querying through all the related characters and fixing them now just going to leave them be
    * once that document which has this deleted character listed as ally/enemy/neutral gets read
    *   THEN it can be updated by checking against the list of names*/
    fun deleteCharacter(storyId: String, charId: String, currentNames: List<String>){
        db.runBatch {batch ->

            //delete character
            batch.delete(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
                    .collection("characters")
                    .document(charId)
            )

            // update names document by removing the name
            batch.update(
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
                    .collection("characters")
                    .document("names"),
                "names",
                currentNames
            )

        }.addOnSuccessListener {
            Log.d(tag, "Story successfully deleted!")
        }.addOnFailureListener{e ->
            Log.w(tag, "Error deleting story", e)
        }
    }

    /* function to get all the in use names for the given title*/
    @Suppress("UNCHECKED_CAST")
    suspend fun getCurrentNames(storyId: String): MutableList<String>?{
        var names: MutableList<String>? = mutableListOf()

        //getting every document in the character subcollection
        db.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("stories")
            .document(storyId)
            .collection("characters")
            .document("names")
            .get()
            .addOnSuccessListener { result ->
                names = result.get("names") as MutableList<String>?
//                val titlesDoc = result.documents.filter { documentSnapshot -> documentSnapshot.id == "names" }[0]
//                names = titlesDoc.get("names") as MutableList<String>?
                Log.i(tag, "success")

            }
            .addOnFailureListener { exception ->
                Log.d(tag, "Error getting documents: ", exception)
                names = null
            }
            .await()
        return names
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
}