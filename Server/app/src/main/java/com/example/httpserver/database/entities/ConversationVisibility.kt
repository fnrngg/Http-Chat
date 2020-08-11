package com.example.httpserver.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversation_visibilities")
data class ConversationVisibility(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userMappingId: Long,
    val userId: Long
)