package com.example.ideawhirl.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.model.NotePalette
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteUiState(
    val isLoading: Boolean,
    val isEditing: Boolean,
)

data class NoteState(
    val note: Note,
    val tagsAdded: List<String> = emptyList(),
    val tagsRemoved: List<String> = emptyList(),
) {
    companion object {
        fun dummy() = NoteState(
            note = Note(
                name = "",
                detail = "",
                tags = emptyList(),
                palette = NotePalette.PALETTE_0,
            )
        )
    }
}

class NoteViewModel(
    private val noteId: Int,
    editingAsFirstState: Boolean,
    private val repository: NoteRepo,
): ViewModel() {
    private val _uiState = MutableStateFlow(
        NoteUiState(isLoading = false, isEditing = editingAsFirstState)
    )
    val uiState = _uiState.asStateFlow()

    private val _noteState = MutableStateFlow(
        NoteState.dummy()
    )
    val note = _noteState.map { it.note }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NoteState.dummy().note
    )

    val globalTags = repository.getALlTagNames().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        if (noteId != -1) {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                _noteState.update { NoteState(repository.findNoteByUid(noteId).first()) }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _noteState.update { it.copy(
            note = it.note.copy(name = title)
        ) }
    }

    fun onDetailChanged(detail: String) {
        _noteState.update { it.copy(
            note = it.note.copy(detail = detail)
        ) }
    }

    fun onTagAdded(tag: String) {
        _noteState.update { it.copy(
            note = it.note.copy(tags = it.note.tags + tag),
            tagsAdded = it.tagsAdded + tag
        ) }
    }

    fun onTagRemoved(tag: String) {
        _noteState.update { it.copy(
            note = it.note.copy(tags = it.note.tags - tag),
            tagsRemoved = it.tagsRemoved + tag
        ) }
    }

    fun onSave() {
        viewModelScope.launch {
            repository.saveNote(
                _noteState.value.note,
                _noteState.value.tagsAdded,
                _noteState.value.tagsRemoved
            )
            _noteState.update { NoteState(repository.findNoteByUid(noteId).first()) }
        }
    }

    fun onEdit() {
        _uiState.update { it.copy(isEditing = true) }
    }

    fun onView() {
        _uiState.update { it.copy(isEditing = false) }
    }

    companion object {
        fun provideFactory(
            noteId: Int,
            editingAsFirstState: Boolean,
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteViewModel(noteId, editingAsFirstState, repository) as T
            }
        }
    }
}