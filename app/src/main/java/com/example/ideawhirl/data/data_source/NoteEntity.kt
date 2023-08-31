package com.example.ideawhirl.data.data_source

import androidx.room.ColumnInfo
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
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private val _uid: Int = 0,

    val name: String,
    val detail: String = "",
    val createdAt: Date = Date(),
) {
    val uid get() = _uid
}

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(vararg noteEntities: NoteEntity): List<Long>

    @Delete
    suspend fun delete(noteEntity: NoteEntity)

    @Query("select * from NoteEntity")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("select * from noteentity join tagentity on noteentity.uid = tagentity.noteId")
    fun getAllNotesWithTags(): Flow<Map<NoteEntity, List<TagEntity>>>

    @Query(
        "select *" +
                " from noteentity note join tagentity tag on note.uid = tag.noteId" +
                " where note.name like :name"
    )
    fun findNoteAndTagsByName(name: String): Flow<Map<NoteEntity, List<TagEntity>>>
}