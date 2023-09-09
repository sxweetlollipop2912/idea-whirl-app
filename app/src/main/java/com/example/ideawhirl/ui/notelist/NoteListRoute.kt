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
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notes by noteListViewModel.notes.collectAsStateWithLifecycle()
    val tags by noteListViewModel.tags.collectAsStateWithLifecycle()
    val selectedTags by noteListViewModel.selectedTags.collectAsStateWithLifecycle()
    val availableColors = noteListViewModel.availableColors

    NoteListScreen(
        notes = notes,
        availableStrokeColors = availableColors,
        tags = tags,
        selectedTags = selectedTags,

        onToCreateNote = onToCreateNote,
        onDeleteNote = noteListViewModel::deleteNote,

        onAddTagOption = noteListViewModel::addTagOption,
        onRemoveTagOption = noteListViewModel::removeTagOption,
        onSelectAllTags = noteListViewModel::selectAllTags,

        onToNote = onToNote,
        onBack = onBack,

        modifier = modifier
    )
}