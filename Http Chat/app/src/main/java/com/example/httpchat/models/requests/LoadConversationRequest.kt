package com.example.httpchat.models.requests

data class LoadConversationRequest(
    val userIdOne: Long,
    val userIdTwo: Long
)