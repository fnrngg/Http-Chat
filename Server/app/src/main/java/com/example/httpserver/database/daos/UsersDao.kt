package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.User

@Dao
interface UsersDao {

    @Query("select * from users u where u.name = :name limit 1")
    fun getUserByName(name: String): User

    @Query("select * from users")
    fun getAllUsers(): List<User>

    @Insert
    fun insertUser(user: User)

    @Query("delete from users")
    fun truncateTable()

    @Delete
    fun deleteUser(user: User)
}