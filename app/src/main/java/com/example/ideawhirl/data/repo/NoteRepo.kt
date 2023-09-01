package com.example.ideawhirl.data.repo

import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteEntity
import com.example.ideawhirl.data.data_source.TagEntity
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
                createdAt = entry.key.createdAt,
                tags = entry.value.map { it.name },
            )
            note
        }
    }

    suspend fun insert(note: Note): Note {
        val noteEntity = NoteEntity(
            name = note.name,
            detail = note.detail,
        )
        val noteId = database.noteDao().insert(noteEntity)[0]
        val tagEntities = note.tags.map { TagEntity(noteId = noteId.toInt(), it) }
        database.tagDao().insert(*tagEntities.toTypedArray())

        val noteEntityFromDB = database.noteDao().findNoteByUid(noteId.toInt()).first()
        val tagEntitiesFromDB = database.tagDao().findTagsByNoteIds(listOf(noteEntityFromDB.uid)).first()
        return noteEntityMapToNote(mapOf(noteEntityFromDB to tagEntitiesFromDB))[0]
    }

    suspend fun delete(note: Note) {
        val noteEntity = NoteEntity(
            _uid = note.uid,
            name = note.name,
            detail = note.detail,
            createdAt = note.createdAt!!,
        )
        database.noteDao().delete(noteEntity)
    }

    fun findNoteByName(name: String): Flow<List<Note>> {
        val entities = database.noteDao().findNoteAndTagsByName(name)
        return entities.map { noteEntityMapToNote(it) }
    }

    fun findNotesByTags(tags: List<String>): Flow<List<Note>> {
        val entities = database.noteDao().findNoteAndTagsByTags(tags)
        return entities.map { noteEntityMapToNote(it) }
    }

    fun getALlTagNames(): Flow<List<String>> {
        return database.tagDao().getAllTagNames()
    }
}