package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.ConversationVisibility

@Dao
interface ConversationVisibilityDao {

    @Query("select count(c.userId) from conversation_visibilities c where c.userMappingId = :userMappingId")
    fun conversationVisibilitiesQuantityFor(userMappingId: Long): Int

    @Query("select count(c.userId) from conversation_visibilities c where c.userId = :userId and c.userMappingId = :userMappingId limit 1")
    fun conversationIsVisibleFor(userId: Long, userMappingId: Long): Boolean

    @Query("delete from conversation_visibilities where userId = :userId and userMappingId = :userMappingId ")
    fun deleteConversationFor(userId: Long, userMappingId: Long)

    @Insert
    fun insertConversationVisibility(conversationVisibility: ConversationVisibility)

    @Query("delete from conversation_visibilities")
    fun truncateTable()

    @Delete
    fun deleteConversationVisibility(conversationVisibility: ConversationVisibility)
}