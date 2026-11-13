package com.example.choralefbbt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Songbook::class, SongSheet::class, Contain::class], version = 1, exportSchema = false)
abstract class ChoraleDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: ChoraleDatabase? = null

        fun getInstance(context: Context): ChoraleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChoraleDatabase::class.java,
                    "chorale_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}