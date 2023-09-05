package com.example.ideawhirl.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single

class HomeViewModel(
    repository: NoteRepo
) : ViewModel() {

    private val notesFlow = repository.getAll()
    private lateinit var allNotes: List<Note>

    suspend fun getAllNotes() {
        notesFlow.collect {
            allNotes = it
        }
    }

    fun getRandomNoteWithTag(tag: String): Note? {
        var randomNote: Note? = null
        randomNote = if (allNotes.isEmpty()) {
            null
        } else
            allNotes.filter { it.tags.contains(tag) }.random()
        return randomNote
    }

    fun getRandomNote(): Note? {
        var randomNote: Note? = null
        randomNote = if (allNotes.isEmpty()) {
            null
        } else {
            allNotes.random()
        }
        return randomNote
    }

    companion object {
        fun provideFactory(
            repository: NoteRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}