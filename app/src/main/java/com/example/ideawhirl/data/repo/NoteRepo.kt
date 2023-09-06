package com.example.ideawhirl.data.repo

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.example.ideawhirl.ui.components.drawing_board.DrawingData
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteEntity
import com.example.ideawhirl.data.data_source.TagEntity
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.model.NotePalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NoteRepo(val database: LocalDatabase, val context: Context) {
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
                tags = entry.value.map { it.name }.toSet(),
                palette = NotePalette.fromId(entry.key.paletteId),
                drawingData = loadOrCreateDrawingData(entry.key.uid)
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
        /* TODO: Save drawing data into file */

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
        /* TODO: Delete drawing file */
        database.noteDao().delete(noteEntity)
    }

    suspend fun update(note: Note) {
        val oldTags = database.tagDao().findTagsByNoteId(note.uid)
        oldTags.first().forEach {
            deleteTag(it.name, note.uid)
        }

        note.tags.forEach {
            insertTag(it, note.uid)
        }

        val noteEntity = NoteEntity(
            _uid = note.uid,
            name = note.name,
            detail = note.detail,
            createdAt = note.createdAt!!,
            paletteId = note.palette.id,
        )
        /* TODO: Update drawing data into file */
        database.noteDao().update(noteEntity)
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

    private suspend fun insertTag(tag: String, noteId: Int) {
        database.tagDao().insert(TagEntity(noteId = noteId, name = tag))
    }

    private suspend fun deleteTag(tag: String, noteId: Int) {
        database.tagDao().delete(TagEntity(noteId, tag))
    }

    private fun getDrawingFilename(uid: Int): String {
        return "drawing_$uid.data"
    }

    fun loadOrCreateDrawingData(uid: Int): DrawingData {
        val filename = getDrawingFilename(uid)
        try {
            context.openFileInput(filename).bufferedReader().useLines { lines ->
                val content = lines.fold("") { content, text ->
                    content.plus(text)
                }
                return Json.decodeFromString(content)
            }
        } catch (e: Throwable) {
            return DrawingData.emptyData()
        }
    }

    fun saveDrawingData(uid: Int, drawingData: DrawingData) {
        val filename = getDrawingFilename(uid)
        if (drawingData == null) {
            throw AssertionError("Drawing data is not initialized.")
        }

        if (uid == 0) {
            throw AssertionError("Note must be fetched from database to save.")
        }

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Json.encodeToString(drawingData).toByteArray())
        }
    }

    fun getAvailableColors(): List<Color> {
        return listOf(
            Color(0XFFFF1F31),
            Color(0XFFFF8B00),
            Color(0XFFFFD200),
            Color(0XFF00C344),
            Color(0XFF456EFE),
            Color(0XFF8C5AFF),
            Color(0XFFFF7AAD),
            Color(0XFFAA7942),
            Color(0xFF000000),
            Color(0xFFFFFFFF),
        )
    }
}