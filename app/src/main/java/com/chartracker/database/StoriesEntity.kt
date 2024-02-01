package com.chartracker.database

data class StoriesEntity (
    val title: String? = null,
    val genre: String? = null,
    val type: String? = null,
    val author: String? = null,
    var imageFilename: String? = null,
    var imagePublicUrl: String? = null
)