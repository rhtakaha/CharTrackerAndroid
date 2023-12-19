package com.chartracker.database

//TODO not sure what might be necessary here. perhaps more settings
// but for now email since we need something and it fits the design to have a user entity
data class UserEntity(
    val email: String? = null
)
