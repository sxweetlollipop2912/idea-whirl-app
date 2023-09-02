package com.example.ideawhirl.ui.notelist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ideawhirl.model.Note

@Composable
fun NoteListScreen(
    onInsertMockNote: () -> Unit,
    notes: List<Note>,
    tags: List<String>,
    selectedTags: List<String>,
    onAddTagOption: (String) -> Unit,
    onRemoveTagOption: (String) -> Unit,
    onSelectAllTags: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onToNote: (Int) -> Unit,
    onTOCreateNote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Button(onClick = onInsertMockNote) {
            Text(text = "Insert mock note")
        }
        Row {
            // an "all" tag
            val isAllSelected = selectedTags.isEmpty()
            Button(
                onClick = {
                    if (!isAllSelected) {
                        onSelectAllTags()
                    }
                },
                colors = if (isAllSelected) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.outline
                    )
                }
            ) {
                Text(text = "All")
            }
            tags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                Button(
                    onClick = {
                        if (!isSelected) {
                            onAddTagOption(tag)
                        } else {
                            onRemoveTagOption(tag)
                        }
                    },
                    colors = if (isSelected) {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.outline
                        )
                    }
                ) {
                    Text(text = tag)
                }
            }
        }
        notes.forEach { note ->
            Button(onClick = { onDeleteNote.invoke(note) }) {
                Column {
                    Text(text = note.name)
                    Text(text = note.detail)
                    Text(text = note.tags.toString())
                }
            }
        }
    }
}