package com.example.httpserver.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_users")
data class ActiveUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long
)