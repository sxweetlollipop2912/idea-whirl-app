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
            name = "test note", detail = "test detail", tag = listOf(
                "test tag 1",
                "test tag 2",
            ), context = context
        )
        runBlocking {
            noteRepo.insert(note)
            val notes = noteRepo.getAll().first()
            TestCase.assertEquals(1, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tag, notes[0].tag)
        }
    }

    @Test
    fun multipleInsertAndGet() {
        val note = Note(
            name = "test note", detail = "test detail", tag = listOf(
                "test tag 1",
                "test tag 2",
            ), context = context
        )
        val note2 = Note(
            name = "test note 2", detail = "test detail 2", tag = listOf(
                "test tag 3",
                "test tag 4",
                "test tag 5",
                "test tag 6",
                "test tag 7",
            ),
            context = context
        )
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val notes = noteRepo.getAll().first()
            TestCase.assertEquals(2, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tag, notes[0].tag)
            TestCase.assertEquals(note2.name, notes[1].name)
            TestCase.assertEquals(note2.detail, notes[1].detail)
            TestCase.assertEquals(note2.tag, notes[1].tag)
        }
    }

    @Test
    fun insertAndFindByName() {
        val note = Note(
            name = "test note", detail = "test detail",
            tag = listOf(
                "test tag 1",
                "test tag 2",
            ),
            context = context,
        )
        val note2 = Note(
            name = "test note 2", detail = "test detail 2",
            tag = listOf(
                "test tag 3",
                "test tag 4",
                "test tag 5",
                "test tag 6",
                "test tag 7",
            ),
            context = context,
        )
        runBlocking {
            noteRepo.insert(note)
            noteRepo.insert(note2)
            val notes = noteRepo.findNoteByName("test note").first()
            TestCase.assertEquals(1, notes.size)
            TestCase.assertEquals(note.name, notes[0].name)
            TestCase.assertEquals(note.detail, notes[0].detail)
            TestCase.assertEquals(note.tag, notes[0].tag)
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
        val note = Note(
            name = "test note", detail = "test detail",
            tag = listOf(
                "test tag 1",
                "test tag 2",
            ),
            context = context,
        )
        val note2 = Note(
            name = "test note 2", detail = "test detail 2",
            tag = listOf(
                "test tag 3",
                "test tag 4",
                "test tag 5",
                "test tag 6",
                "test tag 7",
            ),
            context = context,
        )
        val drawingData = generateDrawingData(1)
        note.drawingData = drawingData
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
            TestCase.assertEquals(note.tag, notes[0].tag)
            TestCase.assertEquals(note.drawingData, notes[0].drawingData)
        }
    }
}
