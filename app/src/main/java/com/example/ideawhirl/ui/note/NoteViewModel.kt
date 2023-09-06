package com.example.ideawhirl.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ideawhirl.components.drawing_board.DrawingData
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
    val isEditingTitle: Boolean,
    val isEditingContent: Boolean,
) {
    val isInEditMode: Boolean
        get() = isEditingTitle || isEditingContent
}

data class NoteState(
    val note: Note,
) {
    companion object {
        fun dummy() = NoteState(
            note = Note.dummy()
        )
    }
}

class NoteViewModel(
    private val noteId: Int,
    private val repository: NoteRepo,
): ViewModel() {
    private val createNote: Boolean
        get() = noteId == -1

    private val _uiState = MutableStateFlow(
        NoteUiState(
            isLoading = false,
            isEditingTitle = false,
            isEditingContent = createNote,
        )
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
        if (createNote) {
            _noteState.update { NoteState.dummy() }
        } else {
            _uiState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                _noteState.update { NoteState(repository.findNoteByUid(noteId).first()) }
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onRequestTitleEdit() {
        _uiState.update { it.copy(isEditingTitle = true) }
    }

    fun onTitleChanged(title: String) {
        _noteState.update { it.copy(
            note = it.note.copy(name = title)
        ) }
    }

    fun onTitleEditDone() {
        _uiState.update { it.copy(isEditingTitle = false) }
    }

    fun onContentChanged(detail: String) {
        _noteState.update { it.copy(
            note = it.note.copy(detail = detail)
        ) }
    }

    fun onTagAdded(tag: String) {
        _noteState.update { it.copy(
            note = it.note.copy(tags = it.note.tags + tag),
        ) }
    }

    fun onTagUpdated(tag: String, newTag: String) {
//        TODO: fix update anomalies
        _noteState.update { it.copy(
            note = it.note.copy(tags = it.note.tags - tag + newTag),
        ) }
    }

    fun onTagRemoved(tag: String) {
        _noteState.update { it.copy(
            note = it.note.copy(tags = it.note.tags - tag),
        ) }
    }

    fun onPaletteChanged(palette: NotePalette) {
        _noteState.update { it.copy(
            note = it.note.copy(
                palette = palette
            )
        ) }
    }

    fun onDeleteDrawing() {
        _noteState.update { it.copy(
            note = it.note.copy(
                drawingData = DrawingData.emptyData()
            )
        ) }
    }

    fun onSave() {
        if (createNote) {
            viewModelScope.launch {
                val noteCreated = repository.insert(
                    _noteState.value.note,
                )
                _noteState.update { NoteState(noteCreated) }
            }
        } else {
            viewModelScope.launch {
                repository.update(
                    _noteState.value.note,
                )
                _noteState.update { NoteState(repository.findNoteByUid(noteId).first()) }
            }
        }
    }

    fun onEditMode() {
        _uiState.update { it.copy(isEditingContent = true) }
    }

    fun onViewMode() {
        _uiState.update { it.copy(
            isEditingContent = false,
            isEditingTitle = false,
        ) }
    }

    companion object {
        fun provideFactory(
            noteId: Int,
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteViewModel(noteId, repository) as T
            }
        }
    }
}