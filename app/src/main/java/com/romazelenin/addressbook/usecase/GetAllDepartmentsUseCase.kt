package com.romazelenin.addressbook.usecase

import com.romazelenin.addressbook.data.AppDatabase
import javax.inject.Inject

class GetAllDepartmentsUseCase @Inject constructor(private val appDatabase: AppDatabase) {
    private val departmentDao = appDatabase.getDepartmentDao()

    operator fun invoke() = departmentDao.getAllDepartments()

}