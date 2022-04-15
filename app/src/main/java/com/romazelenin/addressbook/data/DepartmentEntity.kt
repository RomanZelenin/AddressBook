package com.romazelenin.addressbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DepartmentEntity(
    @PrimaryKey val label: String,
    val name: String,
)
