package com.example.ideawhirl.data.data_source

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.ideawhirl.data.DateTimeConverter

@Database(entities = [NoteEntity::class, TagEntity::class], version = 1)
@TypeConverters(DateTimeConverter::class)
abstract class LocalDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun tagDao(): TagDao
    companion object {
        @Volatile
        private var INSTANCE: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
//                    "idea-whirl-app-database",
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}