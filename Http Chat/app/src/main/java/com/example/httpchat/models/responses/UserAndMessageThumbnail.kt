package com.example.httpchat.models.responses

data class UserAndMessageThumbnail(
    val user: User,
    val message: Message?
)