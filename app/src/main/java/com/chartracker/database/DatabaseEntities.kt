package com.chartracker.database

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp

/* File holding the entities in the database*/

open class DatabaseEntity(
    name: String = "",
    imageFilename: String? = null,
    imagePublicUrl: String? = null
){
    val name: MutableState<String> = mutableStateOf(name)
    val imageFilename: MutableState<String?> = mutableStateOf(imageFilename)
    val imagePublicUrl: MutableState<String?> = mutableStateOf(imagePublicUrl)
    val accessDate: MutableState<Timestamp> = mutableStateOf(Timestamp(java.util.Date()))
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

    fun toHashMap(): HashMap<String, Any?>{
        return hashMapOf(
            "name" to name.value,
            "genre" to genre.value,
            "type" to type.value,
            "author" to author.value,
            "imageFilename" to imageFilename.value,
            "imagePublicUrl" to imagePublicUrl.value,
            "accessDate" to accessDate.value
        )
    }
}

class CharacterEntity(
    name: String = "",
    aliases: String = "",
    titles: String = "",
    age: String = "",
    home: String = "",
    gender: String = "",
    race: String = "",
    livingOrDead: String = "",
    occupation: String = "",
    weapons: String = "",
    toolsEquipment: String = "",
    bio: String = "",
    faction: String = "", //might want to make faction objects down the line
    allies: List<String>? = null,
    enemies: List<String>? = null,
    neutral: List<String>? = null,
    imageFilename: String? = null,
    imagePublicUrl: String? = null
): DatabaseEntity(name = name, imageFilename = imageFilename, imagePublicUrl = imagePublicUrl){
    val aliases: MutableState<String> = mutableStateOf(aliases)
    val titles: MutableState<String> = mutableStateOf(titles)
    val age: MutableState<String> = mutableStateOf(age)
    val home: MutableState<String> = mutableStateOf(home)
    val gender: MutableState<String> = mutableStateOf(gender)
    val race: MutableState<String> = mutableStateOf(race)
    val livingOrDead: MutableState<String> = mutableStateOf(livingOrDead)
    val occupation: MutableState<String> = mutableStateOf(occupation)
    val weapons: MutableState<String> = mutableStateOf(weapons)
    val toolsEquipment: MutableState<String> = mutableStateOf(toolsEquipment)
    val bio: MutableState<String> = mutableStateOf(bio)
    val faction: MutableState<String> = mutableStateOf(faction)
    val allies: MutableState<List<String>?> = mutableStateOf(allies)
    val enemies: MutableState<List<String>?> = mutableStateOf(enemies)
    val neutral: MutableState<List<String>?> = mutableStateOf(neutral)

    fun toHashMap(): HashMap<String, Any?>{
        return  hashMapOf(
            "name" to name.value,
            "aliases" to aliases.value,
            "titles" to titles.value,
            "age" to age.value,
            "home" to home.value,
            "gender" to gender.value,
            "race" to race.value,
            "livingOrDead" to livingOrDead.value,
            "occupation" to occupation.value,
            "weapons" to weapons.value,
            "toolsEquipment" to toolsEquipment.value,
            "bio" to bio.value,
            "faction" to faction.value,
            "allies" to allies.value,
            "enemies" to enemies.value,
            "neutral" to neutral.value,
            "imageFilename" to imageFilename.value,
            "imagePublicUrl" to imagePublicUrl.value,
            "accessDate" to accessDate.value
        )
    }
}