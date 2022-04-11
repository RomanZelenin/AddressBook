package com.romazelenin.addressbook.di

import android.content.Context
import androidx.room.Room
import com.romazelenin.addressbook.data.AppDatabase
import com.romazelenin.addressbook.data.ImplUserRepository
import com.romazelenin.addressbook.domain.UserRepository
import com.romazelenin.addressbook.domain.UsersServiceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    fun provideAppDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
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