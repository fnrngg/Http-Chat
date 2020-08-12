package com.example.httpchat.retrofit

import com.example.httpchat.models.parameterclasses.GetConversationHistoryParams
import com.example.httpchat.models.parameterclasses.SearchParameters
import com.example.httpchat.models.parameterclasses.deleteConversationParams
import com.example.httpchat.models.parameterclasses.sendMessageParams
import com.example.httpchat.models.requests.LoadConversationRequest
import com.example.httpchat.models.requests.UserLoginRequest
import com.example.httpchat.models.responses.Message
import com.example.httpchat.models.responses.User
import com.example.httpchat.models.responses.UserAndMessageThumbnail
import io.reactivex.Single
import retrofit2.http.*

interface Api {
    //TODO vaxo's work
    //loads single conversation between users
    @POST("messages/loadConversation")
    fun loadConversation(
        @Body loadConversationRequest: LoadConversationRequest
    ): Single<List<Message>>

    @Headers(
        "Content-type: application/json"
    )
    @POST("login")
    fun userLogin(
        @Body userLoginRequest: UserLoginRequest
    ): Single<User>

    @Multipart
    @POST("allAvailableUsers")
    fun getAvailableUsers(
        @Part userId: Long
    ): Single<List<User>>

    //TODO incomplete
    @POST("messages/loadConversationThumbnails")
    fun loadConversationHistory(
        @Body params: GetConversationHistoryParams
    ): Single<List<UserAndMessageThumbnail>>

    //TODO incomplete
    @POST("messages/searchConversation")
    fun searchConversation(
        @Body SearchParameters: SearchParameters
    ): Single<List<UserAndMessageThumbnail>>

    @POST("messages/deleteConversation")
    fun deleteConversation(
        @Body params: deleteConversationParams
    ): Single<String>

    @POST("messages/sendMessage")
    fun sendMessage(
        @Body params: sendMessageParams
    ): Single<String>


}