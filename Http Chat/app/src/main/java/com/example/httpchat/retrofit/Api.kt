package com.example.httpchat.retrofit

import com.example.httpchat.models.User
import com.example.httpchat.models.requests.LoadConversationRequest
import com.example.httpchat.models.requests.UserLoginRequest
import com.example.httpchat.models.responses.Message
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


    @POST("login")
    fun userLogin(
        @Body userLoginRequest : UserLoginRequest
    ): Single<String>

    @Multipart
    @POST("allAvailableUsers")
    fun getAvailableUsers(
        @Part userId: Long
    ): Single<List<com.example.httpchat.models.responses.User>>

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


    //TODO beqo's work
    @GET("messages/")
    fun getConversationFrom(
        @Query("myId") myId: String,
        @Query("userId") userId: String
    ): Single<List<String>>

    @GET("messages/from/")
    fun getAllConversations(
        @Query("myId") myId: String,
        @Query("userNickname") userNickname: String,
        @Query("loadedNum") loadedNum: Int
    ): Single<List<String>>

    @POST("user/")
    fun saveUser(@Body user: User): Single<String>

    @Multipart
    @POST("delete")
    fun deleteConversation(@Part from: String, @Part to: String): Single<Unit>

    @Multipart
    @POST("send")
    fun sendMessage(@Part from: String, @Part to: String, @Part message: String): Single<Unit>
}