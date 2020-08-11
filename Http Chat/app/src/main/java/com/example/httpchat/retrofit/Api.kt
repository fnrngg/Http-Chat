package com.example.httpchat.retrofit

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
        @Body loadConversationRequest : LoadConversationRequest
    ): Single<List<Message>>

    @Headers(value = [
        "Accept:application/json",
        "Content-type:application/json",
        "Cache-Control: no-cache"]
    )
    @POST("login")
    fun userLogin(
        @Body userLoginRequest : UserLoginRequest
    ): Single<User>

    @Multipart
    @POST("allAvailableUsers")
    fun getAvailableUsers(
        @Part userId: Long
    ): Single<List<User>>

    //TODO incomplete
    @Multipart
    @POST("messages/loadConversationThumbnails")
    fun loadConversationHistory(
        @Part userId: Long,
        @Part loadIndex: Long
    ): Single<List<UserAndMessageThumbnail>>

    //TODO incomplete
    @Multipart
    @POST("messages/searchConversation")
    fun searchConversation(
        @Part userIdOne: Long,
        @Part userNameTwo: String
    ): Single<List<UserAndMessageThumbnail>>

    @Multipart
    @POST("messages/deleteConversation")
    fun deleteConversation(
        @Part userId: Long,
        @Part userMappingId: Long
    ): Single<String>

    @Multipart
    @POST("messages/sendMessage")
    fun sendMessage(
        @Part from : Long,
        @Part to : Long,
        @Part text : String
    ) : Single<String>


}