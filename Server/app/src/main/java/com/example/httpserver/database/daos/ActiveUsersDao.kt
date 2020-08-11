package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.ActiveUser
import com.example.httpserver.database.entities.User

@Dao
interface ActiveUsersDao {

    @Query("select u.* from users u join active_users a on a.userId = u.id where u.id <> :userId")
    fun getActiveUsersFor(userId: Long): ArrayList<User>

    @Query("select count(a.userId) from active_users a where a.userId = :userId limit 1")
    fun checkIfUserIsLoggedIn(userId: Long): Boolean

    @Query("select * from active_users")
    fun getAllActiveUsers(): ArrayList<ActiveUser>

    @Insert
    fun insertActiveUser(activeUser: ActiveUser)

    @Query("delete from active_users")
    fun truncateTable()

    @Delete
    fun deleteActiveUser(activeUser: ActiveUser)
}