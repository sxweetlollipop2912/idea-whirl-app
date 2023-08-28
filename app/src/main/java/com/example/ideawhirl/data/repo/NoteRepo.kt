package com.example.ideawhirl.data.repo

import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteEntity
import com.example.ideawhirl.data.data_source.TagEntity
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepo(val database: LocalDatabase) {
    fun getAll(): Flow<List<Note>> {
        val entities = database.noteDao().getAllNotesWithTags()
        return entities.map { noteEntityMapToNote(it) }
    }

    private fun noteEntityMapToNote(
        map: Map<NoteEntity, List<TagEntity>>
    ): List<Note>
    {
        return map.map { entry ->
            val note = Note(
                uid = entry.key.uid,
                name = entry.key.name,
                detail = entry.key.detail,
                tag = entry.value.map { it.name }
            )
            note
        }
    }

    suspend fun insert(note: Note) {
        val noteEntity = NoteEntity(
            name = note.name,
            detail = note.detail,
        )
        val noteId = database.noteDao().insert(noteEntity)[0]
        val tagEntities = note.tag.map { TagEntity(noteId = noteId.toInt(), it) }
        database.tagDao().insert(*tagEntities.toTypedArray())
    }

    suspend fun findNoteByName(name: String): Flow<List<Note>> {
        val entities = database.noteDao().findNoteAndTagsByName(name)
        return entities.map { noteEntityMapToNote(it) }
    }
}