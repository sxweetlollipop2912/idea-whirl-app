package com.example.ideawhirl

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepoTest {
    private lateinit var noteRepo: NoteRepo

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
        ).fallbackToDestructiveMigration().build()
        noteRepo = NoteRepo(db)
    }

    @After
    fun closeDb() {
        noteRepo.database.close()
    }

    @Test
    fun insertAndGet() {
        val note = Note(name = "test note", detail = "test detail", tag = listOf(
            "test tag 1",
            "test tag 2",
        ))
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
        val note = Note(name = "test note", detail = "test detail", tag = listOf(
            "test tag 1",
            "test tag 2",
        ))
        val note2 = Note(name = "test note 2", detail = "test detail 2", tag = listOf(
            "test tag 3",
            "test tag 4",
            "test tag 5",
            "test tag 6",
            "test tag 7",
        ))
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
        val note = Note(name = "test note", detail = "test detail", tag = listOf(
            "test tag 1",
            "test tag 2",
        ))
        val note2 = Note(name = "test note 2", detail = "test detail 2", tag = listOf(
            "test tag 3",
            "test tag 4",
            "test tag 5",
            "test tag 6",
            "test tag 7",
        ))
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
}
