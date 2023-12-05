package com.chartracker.database

data class StoriesEntity (
    val name: String,
    val genre: String? = null,
    val type: String? = null,
    val author: String? = null
)