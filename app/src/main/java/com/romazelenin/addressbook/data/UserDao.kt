package com.romazelenin.addressbook.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: EntityUser)

    @Update
    suspend fun update(user: EntityUser)

    @Delete
    suspend fun delete(user: EntityUser)

    @Query("Select * From EntityUser")
    fun getAllUsers(): Flow<List<EntityUser>>

    @Query("Select * From EntityUser Where id = (:userId)")
    fun getUserById(userId: String): Flow<EntityUser>

    @Query("Select * From EntityUser Where firstName Like (:query) or lastName Like (:query) or userTag Like (:query)")
    fun searchUser(query: String): Flow<List<EntityUser>>
}