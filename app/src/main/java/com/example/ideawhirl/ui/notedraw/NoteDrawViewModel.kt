package com.example.ideawhirl.ui.notedraw

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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class NoteDrawViewModel(
    private val noteId: Int,
    private val repository: NoteRepo,
): ViewModel() {
    private val _drawingData = MutableStateFlow(
        DrawingData.emptyData()
    )
    val drawingData = _drawingData.asStateFlow()

    val availableColors = repository.getAvailableColors()

    val palette = repository.findNoteByUid(noteId).map { it.palette }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Note.dummy().palette
    )

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
        _drawingData.update { repository.loadOrCreateDrawingData(noteId) }
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
