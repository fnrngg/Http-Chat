package com.example.httpchat.models.responses

data class User(
    val id: Long,
    val name: String,
    val profession: String,
    val picture: String?
)