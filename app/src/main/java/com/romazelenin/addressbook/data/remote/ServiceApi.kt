package com.romazelenin.addressbook.data.remote

import com.romazelenin.addressbook.data.EntityUser
import retrofit2.http.GET

interface ServiceApi {

    @GET("users")
    suspend fun getUsers(): Response
}

data class Response(val items:List<EntityUser>)