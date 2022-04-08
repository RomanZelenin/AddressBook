package com.romazelenin.addressbook.domain

import com.romazelenin.addressbook.domain.entity.User

interface UsersServiceApi {

    suspend fun getUsers(): List<User>
}