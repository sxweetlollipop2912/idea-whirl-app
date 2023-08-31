package com.example.ideawhirl.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note

class HomeViewModel(
    private val repository: NoteRepo
) : ViewModel() {

    val notes = repository.getAll()

    suspend fun getRandomNote(): Note? {
        var random_note: Note? = null
        notes.collect { newNotes ->
            if (newNotes.isEmpty()) {
                random_note = null
            }
            random_note = newNotes.random()
        }
        return random_note
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