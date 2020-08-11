package com.example.httpserver.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.httpserver.database.entities.UserMapping

@Dao
interface UserMappingsDao {

    @Query("delete from user_mappings where id = :userMappingId")
    fun deleteMappingById(userMappingId: Long)

    @Query("select count(u.userOne) from user_mappings u where u.id = :userMappingId")
    fun checkIfMappingExists(userMappingId: Long): Int

    @Query("select u.* from user_mappings u where u.userOne = :userId or u.userTwo = :userId")
    fun getMappingsForUser(userId: Long): List<UserMapping>

    @Query("select u.* from user_mappings u where (u.userOne =:userOne and u.userTwo = :userTwo) or (u.userOne =:userTwo and u.userTwo = :userOne) limit 1")
    fun getMapping(userOne: Long, userTwo: Long): UserMapping

    @Query("select count(u.id) from user_mappings u where (u.userOne =:userOne and u.userTwo = :userTwo) or (u.userOne =:userTwo and u.userTwo = :userOne) limit 1")
    fun checkIfMappingExists(userOne: Long, userTwo: Long): Boolean

    @Insert
    fun insertUserMapping(userMapping: UserMapping)

    @Query("delete from user_mappings")
    fun truncateTable()

    @Delete
    fun deleteMapping(userMapping: UserMapping)
}