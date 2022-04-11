package com.romazelenin.addressbook.data

import retrofit2.http.GET

interface ServiceApi {

    @GET("users")
    suspend fun getUsers():Response
}

data class Response(val items:List<EntityUser>)