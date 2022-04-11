package com.romazelenin.addressbook.domain

import com.romazelenin.addressbook.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert(user: User)

    suspend fun update(user: User)

    suspend fun delete(user: User)

    fun getAllUsers(): Flow<List<User>>

    fun getUser(userId: Int): Flow<User>

    fun searchUser(query: String): Flow<List<User>>

}