package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.Message

@Dao
interface MessagesDao {

    @Query("select * from messages m where (m.sentFrom = :userId and m.sentTo = :anotherUserId) or (m.sentFrom = :anotherUserId and m.sentTo = :userId) order by m.date asc")
    fun getConversationBetween(userId: Long, anotherUserId: Long): List<Message>

    @Query("select * from messages")
    fun getAllMessages(): List<Message>

    @Insert
    fun insertMessage(message: Message)

    @Query("delete from messages")
    fun truncateTable()

    @Delete
    fun deleteMessage(message: Message)
}