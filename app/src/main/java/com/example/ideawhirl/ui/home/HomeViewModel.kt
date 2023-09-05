package com.example.ideawhirl.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note

class HomeViewModel(
    repository: NoteRepo
) : ViewModel() {

    private val allNotes = repository.getAll()

    suspend fun getRandomNoteWithTag(tag: String): Note? {
        var randomNote: Note? = null
        allNotes.collect { newNotes ->
            if (newNotes.isEmpty()) {
                randomNote = null
            }
            randomNote = newNotes.filter { it.tags.contains(tag) }.random()
        }
        return randomNote
    }

    suspend fun getRandomNote(): Note? {
        var randomNote: Note? = null
        allNotes.collect { newNotes ->
            if (newNotes.isEmpty()) {
                randomNote = null
            }
            randomNote = newNotes.random()
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