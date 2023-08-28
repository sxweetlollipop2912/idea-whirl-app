package com.example.ideawhirl

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ideawhirl.data.data_source.LocalDatabase
import com.example.ideawhirl.data.data_source.NoteDao
import com.example.ideawhirl.data.data_source.NoteEntity
import junit.framework.TestCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.coroutines.coroutineContext

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var noteDao: NoteDao
    private lateinit var db: LocalDatabase

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = LocalDatabase.getDatabase(
            context
        )
        noteDao = db.noteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun createAndSearch() = runBlocking {
    }
}
