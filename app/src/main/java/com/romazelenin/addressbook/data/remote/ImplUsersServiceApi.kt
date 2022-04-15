package com.romazelenin.addressbook.data.remote

import com.romazelenin.addressbook.data.toUser
import com.romazelenin.addressbook.domain.entity.User
import com.romazelenin.addressbook.domain.UsersServiceApi
import javax.inject.Inject

class ImplUsersServiceApi @Inject constructor(private val serviceApi: ServiceApi) :
    UsersServiceApi {
    override suspend fun getUsers(): List<User> {
        return serviceApi.getUsers().items.map { it.toUser() }
    }
}