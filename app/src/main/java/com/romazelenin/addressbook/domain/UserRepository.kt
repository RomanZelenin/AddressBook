package com.romazelenin.addressbook.domain

import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun insert(user: User)

    suspend fun update(user: User)

    suspend fun delete(user: User)

    fun getAllUsers(): Flow<State<out List<User>>>

    fun getUserById(userId: String): Flow<User>

    fun searchUser(query: String): Flow<List<User>>

}