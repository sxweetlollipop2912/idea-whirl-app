package com.example.ideawhirl

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteDao
import com.example.ideawhirl.data.data_source.NoteEntity
import com.example.ideawhirl.data.data_source.TagDao
import com.example.ideawhirl.data.data_source.TagEntity
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseEntityTest {
    private lateinit var noteDao: NoteDao
    private lateinit var tagDao: TagDao
    private lateinit var db: LocalDatabase

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
        ).fallbackToDestructiveMigration().build()
        noteDao = db.noteDao()
        tagDao = db.tagDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun basicInsertWithoutTag(): Unit = runBlocking {
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

    @Test
    fun insertWithTag(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        val tag3 = TagEntity(noteId = notes[1].uid, name = "tag3")
        val tag4 = TagEntity(noteId = notes[1].uid, name = "tag4")
        tagDao.insert(tag1)
        tagDao.insert(tag2)
        tagDao.insert(tag3)
        tagDao.insert(tag4)

        val notesWithTags = noteDao.getAllNotesWithTags()
        val notesWithTagsList = notesWithTags.first()
        TestCase.assertEquals(2, notesWithTagsList.size)
        TestCase.assertEquals(2, notesWithTagsList[notes[0]]?.size)
        TestCase.assertEquals(tag1.name, notesWithTagsList[notes[0]]?.get(0)?.name)
        TestCase.assertEquals(tag2.name, notesWithTagsList[notes[0]]?.get(1)?.name)
        TestCase.assertEquals(2, notesWithTagsList[notes[1]]?.size)
        TestCase.assertEquals(tag3.name, notesWithTagsList[notes[1]]?.get(0)?.name)
        TestCase.assertEquals(tag4.name, notesWithTagsList[notes[1]]?.get(1)?.name)
    }

    @Test
    fun insertWithTagThenFindByName(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        val tag3 = TagEntity(noteId = notes[1].uid, name = "tag3")
        val tag4 = TagEntity(noteId = notes[1].uid, name = "tag4")
        tagDao.insert(tag1)
        tagDao.insert(tag2)
        tagDao.insert(tag3)
        tagDao.insert(tag4)

        val note1 = noteDao.findNoteAndTagsByName(notes[0].name).first().entries.first()
        val note2 = noteDao.findNoteAndTagsByName(notes[1].name).first().entries.first()
        TestCase.assertEquals(notes[0].name, note1.key.name)
        TestCase.assertEquals(notes[0].detail, note1.key.detail)
        TestCase.assertEquals(notes[1].name, note2.key.name)
        TestCase.assertEquals(notes[1].detail, note2.key.detail)
        TestCase.assertEquals(2, note1.value.size)
        TestCase.assertEquals(tag1.name, note1.value[0].name)
        TestCase.assertEquals(tag2.name, note1.value[1].name)
        TestCase.assertEquals(2, note2.value.size)
        TestCase.assertEquals(tag3.name, note2.value[0].name)
        TestCase.assertEquals(tag4.name, note2.value[1].name)
    }

    @Test
    fun findNoteAndTagsByTags(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        val tag3 = TagEntity(noteId = notes[1].uid, name = "tag3")
        val tag4 = TagEntity(noteId = notes[1].uid, name = "tag4")
        tagDao.insert(tag1)
        tagDao.insert(tag2)
        tagDao.insert(tag3)
        tagDao.insert(tag4)

        val result = noteDao.findNoteAndTagsByTags(listOf("tag1", "tag3"))
        val noteWithTagsMap = result.first()
        TestCase.assertEquals(2, noteWithTagsMap.size)
        TestCase.assertEquals(notes[0].name, noteWithTagsMap.keys.first().name)
        TestCase.assertEquals(notes[0].detail, noteWithTagsMap.keys.first().detail)
        TestCase.assertEquals(notes[1].name, noteWithTagsMap.keys.last().name)
        TestCase.assertEquals(notes[1].detail, noteWithTagsMap.keys.last().detail)
        TestCase.assertEquals(2, noteWithTagsMap.values.first().size)
        TestCase.assertEquals(tag1.name, noteWithTagsMap.values.first()[0].name)
        TestCase.assertEquals(tag2.name, noteWithTagsMap.values.first()[1].name)
        TestCase.assertEquals(2, noteWithTagsMap.values.last().size)
        TestCase.assertEquals(tag3.name, noteWithTagsMap.values.last()[0].name)
        TestCase.assertEquals(tag4.name, noteWithTagsMap.values.last()[1].name)
    }

    @Test
    fun findTagsByNoteIds(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        val tag3 = TagEntity(noteId = notes[1].uid, name = "tag3")
        val tag4 = TagEntity(noteId = notes[1].uid, name = "tag4")
        tagDao.insert(tag1)
        tagDao.insert(tag2)
        tagDao.insert(tag3)
        tagDao.insert(tag4)

        val result = tagDao.findTagsByNoteIds(listOf(notes[0].uid, notes[1].uid))
        val tagsList = result.first()
        TestCase.assertEquals(4, tagsList.size)
        TestCase.assertEquals(true, tagsList.contains(tag1))
        TestCase.assertEquals(true, tagsList.contains(tag2))
        TestCase.assertEquals(true, tagsList.contains(tag3))
        TestCase.assertEquals(true, tagsList.contains(tag4))
    }

    @Test
    fun getAllTagNames(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        val tag3 = TagEntity(noteId = notes[1].uid, name = "tag3")
        val tag4 = TagEntity(noteId = notes[1].uid, name = "tag4")
        tagDao.insert(tag1)
        tagDao.insert(tag2)
        tagDao.insert(tag3)
        tagDao.insert(tag4)

        val result = tagDao.getAllTagNames()
        val tagsList = result.first()
        TestCase.assertEquals(4, tagsList.size)
        TestCase.assertEquals(true, tagsList.contains(tag1.name))
        TestCase.assertEquals(true, tagsList.contains(tag2.name))
        TestCase.assertEquals(true, tagsList.contains(tag3.name))
        TestCase.assertEquals(true, tagsList.contains(tag4.name))
    }

    @Test
    fun deleteNote(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        val noteEntity2 = NoteEntity(name = "test note 2", detail = "test detail 2")
        noteDao.insert(noteEntity)
        noteDao.insert(noteEntity2)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        val tag3 = TagEntity(noteId = notes[1].uid, name = "tag3")
        val tag4 = TagEntity(noteId = notes[1].uid, name = "tag4")
        tagDao.insert(tag1)
        tagDao.insert(tag2)
        tagDao.insert(tag3)
        tagDao.insert(tag4)

        noteDao.delete(notes[0])

        val result = tagDao.getAllTagNames()
        val tagsList = result.first()
        TestCase.assertEquals(2, tagsList.size)
        TestCase.assertEquals(false, tagsList.contains(tag1.name))
        TestCase.assertEquals(false, tagsList.contains(tag2.name))
        TestCase.assertEquals(true, tagsList.contains(tag3.name))
        TestCase.assertEquals(true, tagsList.contains(tag4.name))

        val result2 = noteDao.getAll()
        val notesList = result2.first()
        TestCase.assertEquals(1, notesList.size)
        TestCase.assertEquals(notes[1].name, notesList[0].name)
        TestCase.assertEquals(notes[1].detail, notesList[0].detail)
    }

    @Test
    fun findNoteByUid(): Unit = runBlocking {
        val noteEntity = NoteEntity(name = "test note", detail = "test detail")
        noteDao.insert(noteEntity)
        val noteEntities = noteDao.getAll()
        val notes = noteEntities.first()

        val tag1 = TagEntity(noteId = notes[0].uid, name = "tag1")
        val tag2 = TagEntity(noteId = notes[0].uid, name = "tag2")
        tagDao.insert(tag1)
        tagDao.insert(tag2)

        val result = noteDao.findNoteByUid(notes[0].uid)
        val note = result.first()
        TestCase.assertEquals(notes[0].name, note.name)
        TestCase.assertEquals(notes[0].detail, note.detail)
    }
}
