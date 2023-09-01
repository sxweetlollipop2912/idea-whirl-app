package com.example.ideawhirl.ui.notelist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoteListRoute(
    noteListViewModel: NoteListViewModel,
    onToNote: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notes by noteListViewModel.notes.collectAsStateWithLifecycle()
    val fakeTags = listOf("tag1", "tag2", "tag3", "tag4", "tag5")
    NoteListScreen(
        notes = notes,
        tags = fakeTags,
        onToNote = onToNote,
        onBackClick = onBack,
        onChangeTagOptions = { /* TODO */ },
        modifier = modifier,
    )
}