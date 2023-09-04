package com.example.ideawhirl.ui.note

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteRoute(
    noteViewModel: NoteViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by noteViewModel.uiState.collectAsStateWithLifecycle()
    val note by noteViewModel.note.collectAsStateWithLifecycle()

    NoteScreen(
        note = note,
        uiState = uiState,
        onRequestTitleEdit = noteViewModel::onRequestTitleEdit,
        onTitleSubmit = noteViewModel::onTitleEditDone,
        onTitleChanged = noteViewModel::onTitleChanged,
        onContentChanged = noteViewModel::onContentChanged,
        onDoneEditing = {
            noteViewModel.onSave()
            noteViewModel.onViewMode()
        },
        onBack = onBack,
        onRequestToAddNewTags = { },
        onTagClick = noteViewModel::onTagRemoved,
        modifier = modifier
    )
}
