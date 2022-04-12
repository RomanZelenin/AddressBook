package com.romazelenin.addressbook.data

import androidx.room.withTransaction
import com.romazelenin.addressbook.domain.UserRepository
import com.romazelenin.addressbook.domain.UsersServiceApi
import com.romazelenin.addressbook.domain.entity.State
import com.romazelenin.addressbook.domain.entity.User
import kotlinx.coroutines.flow.*
import javax.inject.Inject


class ImplUserRepository @Inject constructor(
    private val appDatabase: AppDatabase,
    private val usersServiceApi: UsersServiceApi
) :
    UserRepository {

    private val userDao = appDatabase.getUserDao()

    override suspend fun insert(user: User) {
        userDao.insert(user.toEntityUser())
    }

    override suspend fun update(user: User) {
        userDao.update(user.toEntityUser())
    }

    override suspend fun delete(user: User) {
        userDao.delete(user.toEntityUser())
    }

    override fun getAllUsers(): Flow<State<out List<User>>> {
        return flow {
            emit(State.Loading)
            val users = usersServiceApi.getUsers()
            appDatabase.withTransaction {
                users.forEach { insert(it) }
            }
            emitAll(userDao.getAllUsers().flatMapConcat { flowOf(State.Success(it.map { it.toUser() })) })
        }.catch { throwable->
            emitAll(userDao.getAllUsers().flatMapConcat { flowOf(State.Failed(throwable,it.map { it.toUser() })) })
        }
    }

    override fun getUserById(userId: String): Flow<User> {
        return userDao.getUserById(userId).map { it.toUser() }
    }

    override fun searchUser(query: String): Flow<List<User>> {
        return userDao.searchUser(query).flatMapConcat { flowOf(it.map { it.toUser() }) }
    }
}

fun User.toEntityUser(): EntityUser {
    return EntityUser(
        id = id,
        avatarUrl = avatarUrl,
        firstName = firstName,
        lastName = lastName,
        userTag = userTag,
        department = department,
        position = position,
        birthday = birthday,
        phone = phone
    )
}

fun EntityUser.toUser(): User {
    return User(
        id = id,
        avatarUrl = avatarUrl,
        firstName = firstName,
        lastName = lastName,
        userTag = userTag,
        department = department,
        position = position,
        birthday = birthday,
        phone = phone
    )
}