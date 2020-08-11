package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.ActiveUser

@Dao
interface ActiveUsersDao {

    @Query("select * from active_users")
    fun getAllActiveUsers(): List<ActiveUser>

    @Insert
    fun insertActiveUser(activeUser: ActiveUser)

    @Query("delete from active_users")
    fun truncateTable()

    @Delete
    fun deleteActiveUser(activeUser: ActiveUser)
}