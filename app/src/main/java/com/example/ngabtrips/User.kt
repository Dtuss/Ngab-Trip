package com.example.ngabtrips

@kotlinx.serialization.Serializable
data class User(
    val id: Int? = null,
    val username: String,
    val password: String
)
