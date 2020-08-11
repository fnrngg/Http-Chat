package com.example.httpchat.models.responses

data class Message(
    val id: Long,
    val text: String,
    val dateMillis: Long,
    val from: Long,
    val userMappingId: Long
)