package com.romazelenin.addressbook.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [EntityUser::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao
}