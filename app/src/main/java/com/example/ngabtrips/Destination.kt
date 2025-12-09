package com.example.ngabtrips

import kotlinx.serialization.Serializable

@Serializable
data class Destination(
    val destination_id: Int,
    val name: String,
    val location: String,
    val description: String
)
