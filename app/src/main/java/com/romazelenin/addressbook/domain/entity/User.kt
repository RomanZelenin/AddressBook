package com.romazelenin.addressbook.domain.entity

data class User(
    val id: String,
    val avatarUrl: String,
    val firstName: String,
    val lastName: String,
    val userTag: String,
    val department: Department,
    val position: String,
    val birthday: String,
    val phone:String,
)
