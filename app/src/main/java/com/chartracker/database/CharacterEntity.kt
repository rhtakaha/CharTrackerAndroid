package com.chartracker.database

data class CharacterEntity(
    val name: String? = null,
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
    val neutral: List<String>? = null
)
