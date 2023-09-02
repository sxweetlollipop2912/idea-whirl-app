package com.example.ideawhirl.ui.note_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val repository: NoteRepo,
) : ViewModel() {
    // emptyList means all tags are selected
    private val _tagOptions = MutableStateFlow(
        emptyList<String>()
    )
    val tagOptions = _tagOptions.asStateFlow()

    // note flow with tag filtering
    private val _notesFlow = repository.getAll().combine(tagOptions) {
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

    val tags = repository.getALlTagNames().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun addTagOption(tag: String) {
        _tagOptions.update { tagOptions ->
            tagOptions + tag
        }
    }

    fun removeTagOption(tag: String) {
        _tagOptions.update { tagOptions ->
            tagOptions - tag
        }
    }

    fun selectAllTags() {
        _tagOptions.update { emptyList() }
    }

    fun insertMockNote() {
        viewModelScope.launch {
            repository.insert(
                Note(
                    name = "test note",
                    detail = "test detail",
                    tags = listOf("tag 1", "tag 2")
                )
            )
            repository.insert(
                Note(
                    name = "test note 2",
                    detail = "test detail 2",
                    tags = listOf("tag 3", "tag 4")
                )
            )
        }
    }

    companion object {
        fun provideFactory(
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteListViewModel(repository) as T
            }
        }
    }
}
