package com.example.ideawhirl.data.data_source

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private val _uid: Int = 0,

    val name: String,
    val detail: String = "",
    val createdAt: Date = Date(),
    val paletteId: Int,
) {
    val uid get() = _uid
}

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(vararg noteEntities: NoteEntity): List<Long>

    @Update
    suspend fun update(noteEntity: NoteEntity)

    @Delete
    suspend fun delete(noteEntity: NoteEntity)

    @Query("select * from NoteEntity")
    fun getAll(): Flow<List<NoteEntity>>

    @Query("select * from noteentity left join tagentity on noteentity.uid = tagentity.noteId")
    fun getAllNotesWithTags(): Flow<Map<NoteEntity, List<TagEntity>>>

    // get notes by using OR operator on tags
    @Query(
        "select * from noteentity note join tagentity tag on note.uid = tag.noteId" +
                " where exists (" +
                "   select * from tagentity where tagentity.noteId = note.uid and tagentity.name in (:tags)" +
                " )"
    )
    fun findNoteAndTagsByTags(tags: List<String>): Flow<Map<NoteEntity, List<TagEntity>>>

    @Query(
        "select *" +
                " from noteentity note left join tagentity tag on note.uid = tag.noteId" +
                " where note.name like :name"
    )
    fun findNoteAndTagsByName(name: String): Flow<Map<NoteEntity, List<TagEntity>>>

    @Query("select * from noteentity where noteentity.uid = :uid")
    fun findNoteByUid(uid: Int): Flow<NoteEntity>
}