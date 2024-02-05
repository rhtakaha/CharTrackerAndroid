package com.chartracker.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/* File holding the entities in the database*/

open class DatabaseEntity(
    name: String = "",
    imageFilename: String? = null,
    imagePublicUrl: String? = null
){
    val name: MutableState<String> = mutableStateOf(name)
    val imageFilename: MutableState<String?> = mutableStateOf(imageFilename)
    val imagePublicUrl: MutableState<String?> = mutableStateOf(imagePublicUrl)
}

class StoryEntity(
    name: String = "",
    genre: String = "",
    type: String = "",
    author: String = "",
    imageFilename: String? = null,
    imagePublicUrl: String? = null
): DatabaseEntity(name = name, imageFilename = imageFilename, imagePublicUrl = imagePublicUrl){
    val genre: MutableState<String> = mutableStateOf(genre)
    val type: MutableState<String> = mutableStateOf(type)
    val author: MutableState<String> = mutableStateOf(author)

    fun toHashMap(): HashMap<String, String?>{
        return hashMapOf(
            "name" to name.value,
            "genre" to genre.value,
            "type" to type.value,
            "author" to author.value,
            "imageFilename" to imageFilename.value,
            "imagePublicUrl" to imagePublicUrl.value
        )
    }
}

class CharacterEntity(
    name: String = "",
    val aliases: String? = null,
    val titles: String? = null,
    val age: Int? = null,
    val home: String? = null,
    val gender: String? = null,
    val race: String? = null,
    val livingOrDead: String? = null,
    val occupation: String? = null,
    val weapons: String? = null,
    val toolsEquipment: String? = null,
    val bio: String? = null,
    val faction: String? = null, //might want to make faction objects down the line
    val allies: List<String>? = null,
    val enemies: List<String>? = null,
    val neutral: List<String>? = null,
    imageFilename: String? = null,
    imagePublicUrl: String? = null
): DatabaseEntity(name = name, imageFilename = imageFilename, imagePublicUrl = imagePublicUrl)