package com.chartracker.database

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface CharacterDBInterface {
    /* creates a character document in the given story*/
    suspend fun createCharacter(storyId: String, character: CharacterEntity, currentNames: List<String>): Boolean
    suspend fun getCharacter(storyId: String, charName: String): CharacterEntity

    /*given the document ID of the story return the list of characters*/
    suspend fun getCharacters(storyId: String): MutableList<CharacterEntity>?
    suspend fun getCharacterId(storyId: String, charName: String): String
    suspend fun updateCharacter(storyId: String, charId: String, updatedCharacter: CharacterEntity, currentNames: List<String>?): Boolean

    /* first queries for characters with a relationship with the to be deleted character
        *   modifies those characters to remove the to be deleted character AND deletes the character ATOMICALLY*/
    /* rather than querying through all the related characters and fixing them now just going to leave them be
    * once that document which has this deleted character listed as ally/enemy/neutral gets read
    *   THEN it can be updated by checking against the list of names*/
    fun deleteCharacter(storyId: String, charId: String, currentNames: List<String>)

    /* function to get all the in use names for the given title*/

    suspend fun getCurrentNames(storyId: String): MutableList<String>?
}

class CharacterDB : CharacterDBInterface {
    private val tag = "CharacterDB"
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    /***************************************** CHARACTER ******************************************/

    /* creates a character document in the given story*/
    override suspend fun createCharacter(storyId: String, character: CharacterEntity, currentNames: List<String>): Boolean{
        var ret = true
        try {
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

            }
                .await()
        }catch (exception: Exception){
            ret = false
            Timber.tag(tag).w(exception, "Error creating character")
        }


        return ret
    }

    override suspend fun getCharacter(storyId: String, charName: String): CharacterEntity{
        var character = CharacterEntity()
        try {
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
                        Timber.tag(tag).w("Successfully retrieved the character ")
                    }else{
                        Timber.tag(tag).w("Error: could not find the character ")
                    }
                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error getting character: ")
        }
        return character
    }

    /*given the document ID of the story return the list of characters*/
    override suspend fun getCharacters(storyId: String): MutableList<CharacterEntity>?{
        var characters: MutableList<CharacterEntity>? = mutableListOf()

        try {
            //getting every document in the characters subcollection of that particular story
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .collection("characters")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.id != "names") {
                            characters?.add(buildCharacterFromDocumentSnapshot(document))
                        }

                    }
                    Timber.tag(tag).i("success")

                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).d(exception, "Error getting documents: ")
            characters = null
        }

        return characters
    }

    override suspend fun getCharacterId(storyId: String, charName: String): String{
        var character = ""
        try {
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
                        Timber.tag(tag).w("Successfully retrieved the character ")
                    }else{
                        Timber.tag(tag).w("Error: could not find the character ")
                    }
                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error getting character: ")
        }

        return character
    }

    override suspend fun updateCharacter(storyId: String, charId: String, updatedCharacter: CharacterEntity, currentNames: List<String>?): Boolean{
        var ret = true

        try {
            if (currentNames == null){
                db.collection("users")
                    .document(auth.currentUser!!.uid)
                    .collection("stories")
                    .document(storyId)
                    .collection("characters")
                    .document(charId)
                    .set(updatedCharacter.toHashMap())
                    .addOnSuccessListener { Timber.tag(tag).d("Character successfully updated!") }
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

                }
                    .await()
            }
        }catch (exception: Exception){
            ret = false
            Timber.tag(tag).w(exception, "Error updating character")
        }

        return ret
    }

    /* first queries for characters with a relationship with the to be deleted character
    *   modifies those characters to remove the to be deleted character AND deletes the character ATOMICALLY*/
    /* rather than querying through all the related characters and fixing them now just going to leave them be
    * once that document which has this deleted character listed as ally/enemy/neutral gets read
    *   THEN it can be updated by checking against the list of names*/
    override fun deleteCharacter(storyId: String, charId: String, currentNames: List<String>){
        try {
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
                Timber.tag(tag).d("Story successfully deleted!")
            }
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error deleting story")
        }
    }

    /* function to get all the in use names for the given title*/
    @Suppress("UNCHECKED_CAST")
    override suspend fun getCurrentNames(storyId: String): MutableList<String>?{
        var names: MutableList<String>? = mutableListOf()

        try {
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
                    Timber.tag(tag).i("success")
                }
                .await()
        }catch (exception: Exception){
            Timber.tag(tag).d(exception, "Error getting current names")
            names = null
        }

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

class MockCharacterDB: CharacterDBInterface{
    /*
    * mocks create character
    * if character.name.value is "true" returns true
    * else returns false*/
    override suspend fun createCharacter(
        storyId: String,
        character: CharacterEntity,
        currentNames: List<String>
    ): Boolean {
        return character.name.value == "true"
    }

    /*
    * mocks get character
    * if charName is "Frodo" returns a Frodo CharacterEntity
    * else returns empty CharacterEntity*/
    override suspend fun getCharacter(storyId: String, charName: String): CharacterEntity {
        if (charName == "Frodo"){
            return CharacterEntity(name = "Frodo", home = "Bag End", race = "Hobbit", weapons = "Sting")
        }
        return CharacterEntity()
    }

    /*
    * mocks get characters
    * if storyId is "id" returns a mutable list of characters
    * else returns null*/
    override suspend fun getCharacters(storyId: String): MutableList<CharacterEntity>? {
        if (storyId == "id"){
            return mutableListOf(
                CharacterEntity(name = "Frodo", home = "Bag End", race = "Hobbit", weapons = "Sting"),
                CharacterEntity(name = "Aragorn", weapons = "Anduril"),
                CharacterEntity(name = "Sam")
            )
        }
        return null
    }

    /*
    * mocks get character id
    * if charName is "Frodo" returns "id"
    * else returns ""*/
    override suspend fun getCharacterId(storyId: String, charName: String): String {
        if (charName == "Frodo"){
            return "id"
        }
        return ""
    }

    /*
    * mocks update character
    * if charId is "id" returns true
    * else returns false*/
    override suspend fun updateCharacter(
        storyId: String,
        charId: String,
        updatedCharacter: CharacterEntity,
        currentNames: List<String>?
    ): Boolean {
        return charId == "id"
    }

    override fun deleteCharacter(storyId: String, charId: String, currentNames: List<String>) {
        return
    }

    /*
    * mocks get current names
    * if storyId is "id" returns list of names
    * else returns null*/
    override suspend fun getCurrentNames(storyId: String): MutableList<String>? {
        if (storyId == "id"){
            return mutableListOf("Frodo", "Aragorn", "Sam")
        }
        return null
    }

}