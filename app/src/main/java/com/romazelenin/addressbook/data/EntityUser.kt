package com.romazelenin.addressbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.romazelenin.addressbook.domain.entity.Department

@Entity
data class EntityUser(
    @PrimaryKey val id: String,
    val avatarUrl: String,
    val firstName: String,
    val lastName: String,
    val userTag: String,
    val department: Department,
    val position: String,
    val birthday: String,
    val phone: String,
)
