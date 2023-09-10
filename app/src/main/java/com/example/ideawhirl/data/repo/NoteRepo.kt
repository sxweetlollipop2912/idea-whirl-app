package com.example.ideawhirl.data.repo

import android.content.Context
import com.example.ideawhirl.ui.components.drawing_board.DrawingData
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteEntity
import com.example.ideawhirl.data.data_source.TagEntity
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.theme.NotePalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date

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
                updatedAt = entry.key.updatedAt,
                tags = entry.value.map { it.name }.toSet(),
                palette = NotePalette.fromId(entry.key.paletteId),
                drawingData = loadOrCreateDrawingData(entry.key.uid)
            )
            note
        }
    }

    /** Does not handle creating a new drawing data file **/
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
            updatedAt = note.updatedAt!!,
            paletteId = note.palette.id,
        )
        context.deleteFile(getDrawingFilename(note.uid))
        database.noteDao().delete(noteEntity)
    }

    /** Does not handle drawing data update **/
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
            updatedAt = Date(),
            paletteId = note.palette.id,
        )
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

    /** Does not handle update for updateAt in NoteEntity table **/
    fun saveDrawingData(uid: Int, drawingData: DrawingData) {
        val filename = getDrawingFilename(uid)

        if (uid == 0) {
            throw AssertionError("Note must be fetched from database to save.")
        }

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(Json.encodeToString(drawingData).toByteArray())
        }
    }

    suspend fun initMockData() {
        database.tagDao().deleteAll()
        database.noteDao().deleteAll()

        val longNote = Note(
            name = "Long Note",
            detail = "Three steps for blah blah blah:\n" +
                    "1. step 1\n" +
                    "2. step 2\n" +
                    "3. step 3\n" +
                    "\n" +
                    "My ideas for blah blah blah:\n" +
                    "- climb a **tree**\n" +
                    "- swim to *the end of the world*\n" +
                    "- run at ~~the sun~~\n" +
                    "\n" +
                    "We can use both **bold** and *italic* ***at the same time***.\n" +
                    "\n" +
                    "***~~The same to the other things.~~***\n" +
                    "\n" +
                    "Lorem ipsum dolor, sit amet consectetur adipisicing elit. Iure id possimus assumenda, reiciendis repudiandae facilis iusto quis animi modi nisi, ducimus quod quam doloremque nesciunt tempora mollitia. Iste, doloribus aliquid.\n" +
                    "Debitis voluptatibus provident adipisci neque distinctio velit, ex vero. Dignissimos sit commodi ratione possimus voluptatibus nobis voluptatem laboriosam dolor hic. Corrupti eveniet sequi aspernatur, doloribus deserunt necessitatibus voluptatum illum commodi.\n" +
                    "Vel dolorem hic nulla officia architecto, dolore odio dolor nisi eius qui voluptatibus explicabo optio, aspernatur autem sapiente commodi. Dignissimos quam explicabo sapiente perspiciatis quis voluptate, vitae similique assumenda cumque?\n" +
                    "Voluptatum, harum ipsam explicabo error minima autem accusantium. Error dicta officiis, placeat, soluta autem assumenda fugit aut facilis distinctio delectus dolores exercitationem laudantium at accusantium odio quam ratione explicabo reiciendis!\n" +
                    "Eligendi mollitia fugit velit eveniet odio ducimus sed molestias deleniti? Doloribus temporibus eius architecto, est facilis, laborum laudantium provident beatae voluptatibus in, tenetur recusandae expedita harum maiores nemo animi dolorum!",
            tags = setOf("idea", "study"),
            palette = NotePalette.PALETTE_1,
        )
        val note2 = Note(
            name = "Note 2",
            detail = "This is a note",
            tags = setOf("idea", "study"),
            palette = NotePalette.PALETTE_2,
        )
        val note3 = Note(
            name = "Note 3",
            detail = "This is a note",
            tags = setOf("study", "drawing"),
            palette = NotePalette.PALETTE_3,
        )
        val note4 = Note(
            name = "Note 4",
            detail = "This is a note",
            tags = setOf("hobby", "drawing"),
            palette = NotePalette.PALETTE_4,
        )
        val note5 = Note(
            name = "Note 5",
            detail = "This is a note",
            tags = setOf("idea", "writing"),
            palette = NotePalette.PALETTE_5,
        )
        val note6 = Note(
            name = "Note 6",
            detail = "This is a note",
            tags = setOf("study", "dontwannadoit"),
            palette = NotePalette.PALETTE_1,
        )
        val note7 = Note(
            name = "Note 7",
            detail = "This is a note",
            tags = setOf("study", "idea"),
            palette = NotePalette.PALETTE_2,
        )
        val note8 = Note(
            name = "Note 8",
            detail = "This is a note",
            tags = setOf("hobby", "idea"),
            palette = NotePalette.PALETTE_3,
        )

        insert(note8)
        insert(note7)
        insert(note6)
        insert(note5)
        insert(note4)
        insert(note3)
        insert(note2)
        insert(longNote)
    }
}