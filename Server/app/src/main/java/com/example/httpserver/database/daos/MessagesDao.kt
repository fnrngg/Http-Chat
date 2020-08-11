package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.Message

@Dao
interface MessagesDao {

    @Query("select * from messages m where m.userMappingId = :userMappingId order by m.dateMillis desc limit 1")
    fun getConversationThumbnailBetween(userMappingId: Long): Message

    @Query("select * from messages m where m.userMappingId = :userMappingId order by m.dateMillis asc")
    fun getConversationBetween(userMappingId: Long): ArrayList<Message>

    @Query("select * from messages")
    fun getAllMessages(): ArrayList<Message>

    @Insert
    fun insertMessage(message: Message)

    @Query("delete from messages")
    fun truncateTable()

    @Delete
    fun deleteMessage(message: Message)
}