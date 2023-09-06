package com.example.ideawhirl.ui.noteDrawdraw

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ideawhirl.ui.components.drawing_board.DrawingConfig
import com.example.ideawhirl.ui.components.drawing_board.DrawingData
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteState(
    val note: Note,
) {
    companion object {
        fun dummy() = NoteState(
            note = Note.dummy()
        )
    }
}
class NoteDrawViewModel(
    private val noteId: Int,
    private val repository: NoteRepo,
): ViewModel() {

    private val _drawingData = MutableStateFlow(
        DrawingData.emptyData()
    )

    val availableColors = repository.getAvailableColors()
    private val _note = MutableStateFlow(
        Color(0xFFFFFFFF)
    )
    private val _noteState = MutableStateFlow(
        NoteState.dummy()
    )
    val palette = _noteState.map { it.note.palette }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NoteState.dummy().note.palette
    )

    val drawingData = _drawingData.asStateFlow()

    private val _drawingConfig = MutableStateFlow(
        DrawingConfig()
    )

    val drawingConfig = _drawingConfig.asStateFlow()
    fun onSave() {
        repository.saveDrawingData(noteId, _drawingData.value)
    }

    fun onUpdateDrawingData(drawingData: DrawingData) {
        _drawingData.update { drawingData }
    }

    fun onUpdateDrawingConfig(drawingConfig: DrawingConfig) {
        _drawingConfig.update { drawingConfig }
    }

    init {
        viewModelScope.launch {
            _drawingData.update { repository.loadOrCreateDrawingData(noteId) }
            _noteState.update { NoteState(repository.findNoteByUid(noteId).first()) }
        }
    }
    companion object {
        fun provideFactory(
            noteId: Int,
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteDrawViewModel(noteId, repository) as T
            }
        }
    }
}
