package com.example.httpchat.models.parameterclasses

import retrofit2.http.Part

data class SearchParameters(
    val userIdOne: Long,
    val userNameTwo: String
)

data class GetConversationHistoryParams(
    val userId: Long,
    val loadIndex: Long
)

data class deleteConversationParams(
    val userId: Long,
    val userMappingId: Long
)

data class sendMessageParams(
    val from: Long,
    val to: Long,
    val text: String
)