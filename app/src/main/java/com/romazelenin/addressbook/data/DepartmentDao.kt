package com.romazelenin.addressbook.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {

    @Query("Select * From DepartmentEntity")
    fun getAllDepartments(): Flow<List<DepartmentEntity>>

}