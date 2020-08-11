package com.example.httpserver.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.httpserver.database.daos.ConversationVisibilityDao
import com.example.httpserver.database.daos.MessagesDao
import com.example.httpserver.database.daos.UserMappingsDao
import com.example.httpserver.database.daos.UsersDao
import com.example.httpserver.database.entities.Message
import com.example.httpserver.database.entities.User
import com.example.httpserver.database.entities.UserMapping

@Database(
    entities = [User::class, Message::class, UserMapping::class],
    version = 1
)
abstract class MainDatabase : RoomDatabase() {
    companion object {
        private lateinit var database: MainDatabase
        private var initialized: Boolean = false

        fun getInstance(context: Context): MainDatabase {
            synchronized(this) {
                if (!initialized) {
                    database =
                        Room.databaseBuilder(context, MainDatabase::class.java, "main_database")
                            .fallbackToDestructiveMigration()
                            .build()
                    initialized = true
                }
                return database
            }
        }
    }

    abstract fun getUsersDao(): UsersDao
    abstract fun getMessagesDao(): MessagesDao
    abstract fun getUserMappingsDao(): UserMappingsDao
    abstract fun getConversationVisibilityDao(): ConversationVisibilityDao
}