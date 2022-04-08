package com.romazelenin.addressbook.data

import com.romazelenin.addressbook.domain.entity.User
import retrofit2.http.GET

interface ServiceApi {

    @GET("users")
    suspend fun getUsers():Response
}

data class Response(val items:List<User>)