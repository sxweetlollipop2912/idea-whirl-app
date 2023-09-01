package com.example.ideawhirl.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity(primaryKeys = ["noteId", "name"], foreignKeys = [
    ForeignKey(
        entity = NoteEntity::class,
        parentColumns = ["uid"],
        childColumns = ["noteId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
])
data class TagEntity(
    val noteId: Int,
    val name: String,
)

@Dao
interface TagDao {
    @Insert
    suspend fun insert(vararg tagEntities: TagEntity)

    @Delete
    suspend fun delete(tagEntity: TagEntity)

    @Query("select distinct name from TagEntity")
    fun getAllTagNames(): Flow<List<String>>

    @Query("select * from tagentity where tagentity.noteId = :noteId")
    fun findTagsByNoteId(noteId: Int): Flow<List<TagEntity>>

    @Query("select * from tagentity where tagentity.noteId in (:noteIds)")
    fun findTagsByNoteIds(noteIds: List<Int>): Flow<List<TagEntity>>
}