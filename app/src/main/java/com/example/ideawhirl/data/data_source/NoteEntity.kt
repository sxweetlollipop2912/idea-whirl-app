package com.example.ideawhirl.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity;
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.util.Date

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) private val _uid: Int = 0,
    val name: String,
    val detail: String = "",
    val createdAt: Date = Date(),
) {
    val uid get() = _uid
}

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(vararg noteEntities: NoteEntity)

    @Delete
    suspend fun delete(noteEntity: NoteEntity)

    @Query("select * from NoteEntity")
    fun getAll(): Flow<List<NoteEntity>>
}