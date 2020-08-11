package com.example.httpchat.models.requests

data class UserLoginRequest(
    val name: String,
    val profession: String,
    val picture: String?
)