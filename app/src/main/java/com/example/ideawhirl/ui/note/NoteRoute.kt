package com.example.ideawhirl.ui.note

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NoteRoute(
    noteViewModel: NoteViewModel,
    noteId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NoteScreen(
        onBack = onBack,
        modifier = modifier
    )
}
