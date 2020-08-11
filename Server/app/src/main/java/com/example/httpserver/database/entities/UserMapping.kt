package com.example.httpserver.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_mappings")
data class UserMapping(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userOne: Long,
    val userTwo: Long
)