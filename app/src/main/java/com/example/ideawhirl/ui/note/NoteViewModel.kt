package com.example.ideawhirl.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ideawhirl.data.repo.NoteRepo

class NoteViewModel(
    private val repository: NoteRepo,
    private val id: Int,
) : ViewModel() {

    companion object {
        fun provideFactory(
            repository: NoteRepo,
            id: Int,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteViewModel(repository, id) as T
            }
        }
    }
}
