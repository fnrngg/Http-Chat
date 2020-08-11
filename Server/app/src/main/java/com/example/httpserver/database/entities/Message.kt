package com.example.httpserver.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val date: String,
    val sentFrom: Long,
    val sentTo: Long
)