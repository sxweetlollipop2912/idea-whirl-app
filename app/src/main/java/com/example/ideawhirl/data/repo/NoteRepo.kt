package com.example.ideawhirl.data.repo

import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteEntity
import com.example.ideawhirl.data.data_source.TagEntity
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.model.NotePalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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
                palette = NotePalette.fromId(entry.key.paletteId),
            )
            note
        }
    }

    suspend fun insert(note: Note): Note {
        val noteEntity = NoteEntity(
            name = note.name,
            detail = note.detail,
            paletteId = note.palette.id,
        )
        val noteId = database.noteDao().insert(noteEntity)[0]
        val tagEntities = note.tags.map { TagEntity(noteId = noteId.toInt(), it) }
        database.tagDao().insert(*tagEntities.toTypedArray())

        return findNoteByUid(noteId.toInt()).first()
    }

    suspend fun delete(note: Note) {
        val noteEntity = NoteEntity(
            _uid = note.uid,
            name = note.name,
            detail = note.detail,
            createdAt = note.createdAt!!,
            paletteId = note.palette.id,
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

    fun findNoteByUid(uid: Int): Flow<Note> {
        val noteEntity = database.noteDao().findNoteByUid(uid)
        val tagEntities = database.tagDao().findTagsByNoteId(uid)
        return noteEntity.combine(tagEntities) { note, tags ->
            noteEntityMapToNote(mapOf(note to tags))
        }.map { it[0] }
    }

    suspend fun saveNote(note: Note, tagsAdded: List<String>, tagsDeleted: List<String>): Boolean {
        // TODO: save note, tags, remove deleted tags
        return true
    }

    suspend fun insertTag(noteId: Int, tag: String) {
        database.tagDao().insert(TagEntity(noteId = noteId, name = tag))
    }
}