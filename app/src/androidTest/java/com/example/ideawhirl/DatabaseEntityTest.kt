package com.example.ideawhirl

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteDao
import com.example.ideawhirl.data.data_source.NoteEntity
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var noteDao: NoteDao
    private lateinit var db: LocalDatabase

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
        ).fallbackToDestructiveMigration().build()
        noteDao = db.noteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun basicInsert(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()
        TestCase.assertEquals(2, notes.size)
        TestCase.assertEquals(noteEntity.name, notes[0].name)
        TestCase.assertEquals(noteEntity.detail, notes[0].detail)
        TestCase.assertEquals(noteEntity2.name, notes[1].name)
        TestCase.assertEquals(noteEntity2.detail, notes[1].detail)
    }
}
