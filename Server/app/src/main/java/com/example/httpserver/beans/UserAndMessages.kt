package com.example.httpserver.beans

import com.example.httpserver.database.entities.Message
import com.example.httpserver.database.entities.User

data class UserAndMessages(
    val user: User,
    val messages: List<Message>
)