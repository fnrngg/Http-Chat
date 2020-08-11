package com.example.httpserver.beans

import com.example.httpserver.database.entities.Message
import com.example.httpserver.database.entities.User

data class UserAndMessageThumbnail(
    val user: User,
    val message: Message
)