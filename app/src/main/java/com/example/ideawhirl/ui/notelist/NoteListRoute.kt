package com.example.ideawhirl.ui.notelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteListRoute(
    noteListViewModel: NoteListViewModel,
    onToNote: (Int) -> Unit,
    onToCreateNote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notes by noteListViewModel.notes.collectAsStateWithLifecycle()
    val tags by noteListViewModel.tags.collectAsStateWithLifecycle()
    val selectedTags by noteListViewModel.tagOptions.collectAsStateWithLifecycle()

    NoteListScreen(
        onInsertMockNote = noteListViewModel::insertMockNote,
        notes = notes,
        tags = tags,
        selectedTags = selectedTags,
        onAddTagOption = noteListViewModel::addTagOption,
        onRemoveTagOption = noteListViewModel::removeTagOption,
        onSelectAllTags = noteListViewModel::selectAllTags,
        onDeleteNote = noteListViewModel::deleteNote,
        onToNote = onToNote,
        onTOCreateNote = onToCreateNote,
        modifier = modifier
    )
}