package com.chartracker.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

interface CharacterDBInterface {
    /* creates a character document in the given story*/
    suspend fun createCharacter(
        storyId: String,
        character: CharacterEntity,
        currentNames: List<String>,
        readyToNavToCharacters: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit
    )
    suspend fun getCharacter(
        storyId: String,
        charName: String,
        character: MutableState<CharacterEntity>,
        failedGetCharacter: MutableState<Boolean>)

    /*given the document ID of the story return the list of characters*/
    suspend fun getCharacters(
        storyId: String,
        characters: MutableStateFlow<MutableList<CharacterEntity>>,
        failedGetCharacters: MutableState<Boolean>)
    suspend fun getCharacterId(storyId: String, charName: String): String
    suspend fun updateCharacter(
        storyId: String,
        charId: String,
        updatedCharacter: CharacterEntity,
        currentNames: List<String>,
        updatedName: Boolean,
        hasImage: Boolean,
        deleteImage: () -> Unit,
        readyToNavToCharacters: MutableState<Boolean> = mutableStateOf(false),
        uploadError: MutableState<Boolean> = mutableStateOf(false)
    )

    /* first queries for characters with a relationship with the to be deleted character
        *   modifies those characters to remove the to be deleted character AND deletes the character ATOMICALLY*/
    /* rather than querying through all the related characters and fixing them now just going to leave them be
    * once that document which has this deleted character listed as ally/enemy/neutral gets read
    *   THEN it can be updated by checking against the list of names*/
    fun deleteCharacter(storyId: String, charId: String, currentNames: List<String>)

    /* function to get all the in use names for the given title*/

    suspend fun getCurrentNames(
        storyId: String,
        currentNames: MutableList<String>,
        error: MutableState<Boolean>)
}

class CharacterDB : CharacterDBInterface {
    private val tag = "CharacterDB"
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    /***************************************** CHARACTER ******************************************/

    /* creates a character document in the given story*/
    override suspend fun createCharacter(
        storyId: String,
        character: CharacterEntity,
        currentNames: List<String>,
        readyToNavToCharacters: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit
        ){
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
            .addOnSuccessListener {
                readyToNavToCharacters.value = true
            }
            .addOnFailureListener {exception ->
                Timber.tag(tag).w(exception, "Error creating character")
                if (hasImage){
                    deleteImage()
                }
                uploadError.value = true
            }
    }

    override suspend fun getCharacter(
        storyId: String,
        charName: String,
        character: MutableState<CharacterEntity>,
        failedGetCharacter: MutableState<Boolean>){

        try {
            character.value = buildCharacterFromDocumentSnapshot(db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .collection("characters")
                .whereEqualTo("name", charName)
                .get()
                .await().documents[0])
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error getting character: ")
            failedGetCharacter.value = true
        }
    }

    /*given the document ID of the story return the list of characters*/
    override suspend fun getCharacters(
        storyId: String,
        characters: MutableStateFlow<MutableList<CharacterEntity>>,
        failedGetCharacters: MutableState<Boolean>){

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
                        characters.value.add(buildCharacterFromDocumentSnapshot(document))
                    }

                }
                Timber.tag(tag).i("success")
                characters.value = characters.value.sortedBy { character -> character.name.value }.toMutableList()

            }
            .addOnFailureListener {
                failedGetCharacters.value = true
            }
    }

    override suspend fun getCharacterId(storyId: String, charName: String): String{
        var character = ""
        try {
            character = db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .collection("characters")
                .whereEqualTo("name", charName)
                .get()
                .await().documents[0].id
        }catch (exception: Exception){
            Timber.tag(tag).w(exception, "Error getting character: ")
        }

        return character
    }

    override suspend fun updateCharacter(
        storyId: String,
        charId: String,
        updatedCharacter: CharacterEntity,
        currentNames: List<String>,
        updatedName: Boolean,
        hasImage: Boolean,
        deleteImage: () -> Unit,
        readyToNavToCharacters: MutableState<Boolean> ,
        uploadError: MutableState<Boolean>
    ){

        if (!updatedName){
            // if no name change don't need to change current names
            db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .collection("characters")
                .document(charId)
                .set(updatedCharacter.toHashMap())
                .addOnSuccessListener {
                    Timber.tag(tag).d("Character successfully updated!")
                    readyToNavToCharacters.value = true
                }
                .addOnFailureListener { exception ->
                    // if failed to update the doc remove image if one was uploaded
                    if (hasImage){
                        deleteImage()
                    }
                    uploadError.value = true
                    Timber.tag(tag).w(exception, "Error updating character")
                }
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

            }
                .addOnSuccessListener {
                    Timber.tag(tag).d("Character successfully updated!")
                    readyToNavToCharacters.value = true
                }
                .addOnFailureListener {exception ->
                    // if failed to update the doc remove image if one was uploaded
                    if (hasImage){
                        deleteImage()
                    }
                    uploadError.value = true
                    Timber.tag(tag).w(exception, "Error updating character")
                }
        }
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
    override suspend fun getCurrentNames(
        storyId: String,
        currentNames: MutableList<String>,
        error: MutableState<Boolean>){

        try {
            //getting every document in the character subcollection
            currentNames.addAll(db.collection("users")
                .document(auth.currentUser!!.uid)
                .collection("stories")
                .document(storyId)
                .collection("characters")
                .document("names")
                .get()
                .await().get("names") as MutableList<String>)
        }catch (exception: Exception){
            Timber.tag(tag).d(exception, "Error getting current names")
            error.value = true
        }
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
        currentNames: List<String>,
        readyToNavToCharacters: MutableState<Boolean>,
        uploadError: MutableState<Boolean>,
        hasImage: Boolean,
        deleteImage: () -> Unit
    ) {
        if (character.name.value == "true"){
            readyToNavToCharacters.value = true
        }else{
            if (hasImage){
                deleteImage()
            }
            uploadError.value = true
        }
    }

    /*
    * mocks get character
    * if charName is "Frodo" returns a Frodo CharacterEntity
    * if charName is "Sam" returns a Sam CharacterEntity (with imageFilename = "filename")
    * else returns empty CharacterEntity*/
    override suspend fun getCharacter(
        storyId: String,
        charName: String,
        character: MutableState<CharacterEntity>,
        failedGetCharacter: MutableState<Boolean>) {
        when (charName) {
            "Frodo" -> {
                character.value = CharacterEntity(
                    name = "Frodo",
                    home = "Bag End",
                    race = "Hobbit",
                    weapons = "Sting",
                    allies = listOf("Sam", "Legolas", "Aragorn"),
                    enemies = listOf("Sauron", "Sauruman", "Witch King"),
                    neutral = listOf("Gollum")
                )
            }
            "Sam" -> {
                character.value = CharacterEntity(name = "Sam", race = "Hobbit", imageFilename = "filename")
            }
            else -> {
                failedGetCharacter.value = true
            }
        }
    }

    /*
    * mocks get characters
    * if storyId is "id" sets character state to a mutable list of characters
    * else sets error state to true*/
    override suspend fun getCharacters(
        storyId: String,
        characters: MutableStateFlow<MutableList<CharacterEntity>>,
        failedGetCharacters: MutableState<Boolean>) {
        if (storyId == "id"){
            characters.value.addAll( mutableListOf(
                CharacterEntity(name = "Aragorn", weapons = "Anduril"),
                CharacterEntity(name = "Frodo", home = "Bag End", race = "Hobbit", weapons = "Sting"),
                CharacterEntity(name = "Sam")
            ))
        }else{
            failedGetCharacters.value = true
        }
    }

    /*
    * mocks get character id
    * if charName is "Frodo" returns "id"
    * if charName is "Sauron" returns "DL"
    * else returns ""*/
    override suspend fun getCharacterId(storyId: String, charName: String): String {
        if (charName == "Frodo"){
            return "id"
        }
        if (charName == "Sam"){
            return "potato"
        }
        if (charName == "Sauron"){
            return "DL"
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
        currentNames: List<String>,
        updatedName: Boolean,
        hasImage: Boolean,
        deleteImage: () -> Unit,
        readyToNavToCharacters: MutableState<Boolean> ,
        uploadError: MutableState<Boolean>
    ) {
        if (charId =="id"){
            readyToNavToCharacters.value = true
        }else{
            if (hasImage){
                deleteImage()
            }
            uploadError.value = true
        }
    }

    override fun deleteCharacter(storyId: String, charId: String, currentNames: List<String>) {
        return
    }

    /*
    * mocks get current names
    * if storyId is "id" returns list of names
    * else returns null*/
    override suspend fun getCurrentNames(
        storyId: String,
        currentNames: MutableList<String>,
        error: MutableState<Boolean>) {
        if (storyId == "id"){
            currentNames.addAll( mutableListOf("Frodo", "Aragorn", "Sam", "Sauruman", "Witch King"))
        }else{
            error.value = true
        }
    }

}