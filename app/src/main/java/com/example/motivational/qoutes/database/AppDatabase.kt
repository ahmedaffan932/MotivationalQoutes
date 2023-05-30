package com.example.motivational.qoutes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [QuotModel::class], version = 1)
@TypeConverters(CustomObjectConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): QuotDao

    companion object {
        private const val DATABASE_NAME = "my_app_database"

        // Singleton pattern to ensure only one instance of the database is created
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

