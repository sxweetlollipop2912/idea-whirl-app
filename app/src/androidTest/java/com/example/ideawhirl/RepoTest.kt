package com.example.ideawhirl

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ideawhirl.components.drawing_board.DrawingData
import com.example.ideawhirl.components.drawing_board.DrawingPath
import com.example.ideawhirl.components.drawing_board.EraserPath
import com.example.ideawhirl.components.drawing_board.EraserWidth
import com.example.ideawhirl.components.drawing_board.Stroke
import com.example.ideawhirl.components.drawing_board.StrokeWidth
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class RepoTest {
    private lateinit var noteRepo: NoteRepo
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setup() {
        val db = Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
        ).fallbackToDestructiveMigration().build()
        noteRepo = NoteRepo(db, context)
    }

    @After
    fun closeDb() {
        noteRepo.database.close()
    }

    @Test
    fun insertAndGet() {
        val note = Note(
            name = "test note", detail = "test detail", tags = setOf(
                "test tag 1",
                "test tag 2",
            )
        )
        runBlocking {
            noteRepo.insert(note)
            val notes = noteRepo.getAll().first()
            TestCase.assertEquals(1, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tags, notes[0].tags)
            TestCase.assertEquals(note.palette, notes[0].palette)
        }
    }

    @Test
    fun multipleInsertAndGet() {
        val note = Note(
            name = "test note", detail = "test detail", tags = setOf(
                "test tag 1",
                "test tag 2",
            )
        )
        val note2 = Note(
            name = "test note 2", detail = "test detail 2", tags = setOf(
                "test tag 3",
                "test tag 4",
                "test tag 5",
                "test tag 6",
                "test tag 7",
            )
        )
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val notes = noteRepo.getAll().first()
            TestCase.assertEquals(2, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tags, notes[0].tags)
            TestCase.assertEquals(note.palette, notes[0].palette)
            TestCase.assertEquals(note2.name, notes[1].name)
            TestCase.assertEquals(note2.detail, notes[1].detail)
            TestCase.assertEquals(note2.tags, notes[1].tags)
            TestCase.assertEquals(note2.palette, notes[1].palette)
        }
    }

    @Test
    fun insertAndFindByName() {
        val note = Note(
            name = "test note", detail = "test detail",
            tags = setOf(
                "test tag 1",
                "test tag 2",
            ),
        )
        val note2 = Note(
            name = "test note 2", detail = "test detail 2",
            tags = setOf(
                "test tag 3",
                "test tag 4",
                "test tag 5",
                "test tag 6",
                "test tag 7",
            ),
        )
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val notes = noteRepo.findNoteByName("test note").first()
            TestCase.assertEquals(1, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tags, notes[0].tags)
        }
    }

    private fun generateStroke(seed: Int): Stroke {
        val rand = Random(seed)
        val path: Stroke = if (rand.nextBoolean()) {
            DrawingPath(StrokeWidth.Bold, 0)
        } else {
            EraserPath(EraserWidth.Bold)
        }
        path.start(rand.nextFloat(), rand.nextFloat())
        (0 until rand.nextInt(1, 20)).forEach { _ ->
            path.drawTo(rand.nextFloat(), rand.nextFloat())
        }
        path.finish(rand.nextFloat(), rand.nextFloat())
        return path
    }

    private fun generateDrawingData(seed: Int): DrawingData {
        val rand = Random(seed)
        val strokeList = (0 until rand.nextInt(1, 20))
            .fold(mutableListOf<Stroke>()) { some, _ ->
                some.add(generateStroke(seed))
                some
            }
        return DrawingData(strokeList)
    }

    @Test
    fun insertDrawInsertAndGet() {
        var note = Note(
            name = "test note", detail = "test detail",
            tags = setOf(
                "test tag 1",
                "test tag 2",
            ),
        )
        val note2 = Note(
            name = "test note 2", detail = "test detail 2",
            tags = setOf(
                "test tag 3",
                "test tag 4",
                "test tag 5",
                "test tag 6",
                "test tag 7",
            ),
        )
        val drawingData = generateDrawingData(1)
        note = note.copy(drawingData = drawingData)
        context.openFileOutput("drawing_1.data", Context.MODE_PRIVATE).use {
            it.write(Json.encodeToString(drawingData).toByteArray())
        }
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val notes = noteRepo.findNoteByName("test note").first()
            TestCase.assertEquals(1, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tags, notes[0].tags)
            TestCase.assertEquals(note.palette, notes[0].palette)
            TestCase.assertEquals(note.drawingData, notes[0].drawingData)
        }
    }

    @Test
    fun deleteNote() {
        val note = Note(name = "test note", detail = "test detail", tags = setOf(
            "test tag 1",
            "test tag 2",
        ))
        val note2 = Note(name = "test note 2", detail = "test detail 2", tags = setOf(
            "test tag 3",
            "test tag 4",
        ))
        runBlocking {
            val note = noteRepo.insert(note)
            noteRepo.insert(note2)
            noteRepo.delete(note)
            val notes = noteRepo.getAll().first()
            TestCase.assertEquals(1, notes.size)
            TestCase.assertEquals(note2.name, notes[0].name)
            TestCase.assertEquals(note2.detail, notes[0].detail)
            TestCase.assertEquals(note2.tags, notes[0].tags)
            TestCase.assertEquals(note2.palette, notes[0].palette)
        }
    }

    @Test
    fun findNotesByTags() {
        val note = Note(name = "test note", detail = "test detail", tags = setOf(
            "test tag 1",
            "test tag 2",
        ))
        val note2 = Note(name = "test note 2", detail = "test detail 2", tags = setOf(
            "test tag 3",
            "test tag 4",
        ))
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val notes = noteRepo.findNotesByTags(listOf("test tag 1", "test tag 3")).first()
            TestCase.assertEquals(2, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tags, notes[0].tags)
            TestCase.assertEquals(note.palette, notes[0].palette)
            TestCase.assertEquals(note2.name, notes[1].name)
            TestCase.assertEquals(note2.detail, notes[1].detail)
            TestCase.assertEquals(note2.tags, notes[1].tags)
            TestCase.assertEquals(note2.palette, notes[1].palette)

            val notes2 = noteRepo.findNotesByTags(listOf("test tag 1")).first()
            TestCase.assertEquals(1, notes2.size)
            TestCase.assertEquals(note.name, notes2[0].name)
            TestCase.assertEquals(note.detail, notes2[0].detail)
            TestCase.assertEquals(note.tags, notes2[0].tags)
            TestCase.assertEquals(note.palette, notes2[0].palette)
        }
    }

    @Test
    fun getAllTagNames() {
        val note = Note(name = "test note", detail = "test detail", tags = setOf(
            "test tag 1",
            "test tag 2",
        ))
        val note2 = Note(name = "test note 2", detail = "test detail 2", tags = setOf(
            "test tag 3",
            "test tag 4",
        ))
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val tagNames = noteRepo.getALlTagNames().first()
            TestCase.assertEquals(4, tagNames.size)
            TestCase.assertEquals("test tag 1", tagNames[0])
            TestCase.assertEquals("test tag 2", tagNames[1])
            TestCase.assertEquals("test tag 3", tagNames[2])
            TestCase.assertEquals("test tag 4", tagNames[3])
        }
    }

    @Test
    fun updateNote() {
        val note = Note(name = "test note", detail = "test detail", tags = setOf(
            "test tag 1",
            "test tag 2",
        ))
        val note2 = Note(name = "test note 2", detail = "test detail 2", tags = setOf(
            "test tag 3",
            "test tag 4",
        ))
        runBlocking {
            val note = noteRepo.insert(note)
            val note2 = noteRepo.insert(note2)
            noteRepo.update(
                note.copy(name = "test note updated", tags = setOf("test tag 1", "test tag 3")),
            )
            val noteUpd = noteRepo.findNoteByUid(note.uid).first()
            val note2Db = noteRepo.findNoteByUid(note2.uid).first()
            TestCase.assertEquals(note.uid, noteUpd.uid)
            TestCase.assertEquals("test note updated", noteUpd.name)
            TestCase.assertEquals(note.detail, noteUpd.detail)
            TestCase.assertEquals(setOf("test tag 1", "test tag 3"), noteUpd.tags)
            TestCase.assertEquals(note.palette, noteUpd.palette)
            TestCase.assertEquals(note2.name, note2Db.name)
            TestCase.assertEquals(note2.detail, note2Db.detail)
            TestCase.assertEquals(note2.tags, note2Db.tags)
            TestCase.assertEquals(note2.palette, note2Db.palette)
        }
    }
}
