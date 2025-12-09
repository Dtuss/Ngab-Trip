package com.example.ngabtrips

import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val group_id: Int,
    val destination_id: Int,
    val creator_name: String,
    val group_name: String,
    val meeting_point: String,
    val departure_date: String,
    val max_members: Int,
    val status: String,
    val member: List<String> = emptyList()
)

@Serializable
data class CreateGroupRequest(
    val group_name: String,
    val destination_id: Int,
    val creator_name: String,
    val meeting_point: String,
    val departure_date: String,
    val max_members: Int,
    val member: List<String> = emptyList()
)
