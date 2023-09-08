package com.example.ideawhirl.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    repository: NoteRepo
) : ViewModel() {

    private val _selectedTags = MutableStateFlow(emptyList<String>())

    val selectedTags = _selectedTags.asStateFlow()

    val tags = repository.getALlTagNames().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _notesFlow = repository.getAll().combine(selectedTags) {
            notes, tagOptions ->
        if (tagOptions.isEmpty()) {
            notes
        } else {
            notes.filter { note ->
                note.tags.any { tag ->
                    tagOptions.contains(tag)
                }
            }
        }
    }
    val notes = _notesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun getRandomNote(): Note? {
        val randomNote = if (notes.value.isEmpty()) {
            null
        } else {
            notes.value.random()
        }
        return randomNote
    }

    fun addTagOption(tag: String) {
        _selectedTags.update { selectedTags ->
            selectedTags + tag
        }
    }

    fun removeTagOption(tag: String) {
        _selectedTags.update { selectedTags ->
            selectedTags - tag
        }
    }

    fun selectAllTags() {
        _selectedTags.update { emptyList() }
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