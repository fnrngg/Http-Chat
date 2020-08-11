package com.example.httpchat.retrofit

import com.example.httpchat.models.User
import io.reactivex.Single
import retrofit2.http.*

interface Api {
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