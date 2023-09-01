package com.example.ideawhirl.ui.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NoteListViewModel(
    private val repository: NoteRepo
) : ViewModel() {

    val notes = repository.getAll().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    companion object {
        fun provideFactory(
            repository: NoteRepo
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteListViewModel(repository) as T
            }
        }
    }
}
