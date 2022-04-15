package com.romazelenin.addressbook.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.romazelenin.addressbook.data.AppDatabase
import com.romazelenin.addressbook.data.ImplUserRepository
import com.romazelenin.addressbook.domain.UserRepository
import com.romazelenin.addressbook.domain.UsersServiceApi
import com.romazelenin.addressbook.domain.entity.Department
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

   private val departments = listOf(
        Department.all.name to "All",
        Department.android.name to "Android",
        Department.design.name to "Design",
        Department.analytics.name to "Analytics",
        Department.backend.name to "Backend",
        Department.back_office.name to "Back Office",
        Department.frontend.name to "Frontend",
        Department.hr.name to "HR",
        Department.ios.name to "iOS",
        Department.management.name to "Management",
        Department.pr.name to "PR",
        Department.qa.name to "QA",
        Department.support.name to "Support"
    )

    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).addCallback(object :
            RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.beginTransaction()
                try {
                    departments.forEach {
                        db.execSQL("Insert Into DepartmentEntity (label, name) values (\"${it.first}\",\"${it.second}\")")
                    }
                    db.setTransactionSuccessful()
                }finally {
                    db.endTransaction()
                }
            }
        })
            .build()
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        appDatabase: AppDatabase,
        usersServiceApi: UsersServiceApi
    ): UserRepository {
        return ImplUserRepository(appDatabase, usersServiceApi)
    }
}