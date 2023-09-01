package com.example.ideawhirl.ui.notelist

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.components.TagPill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    tags: List<String>,
    onToNote: (String) -> Unit,
    onBackClick: () -> Unit,
    onChangeTagOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Notes")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        NoteListScreenContent(
            notes = notes,
            tags = tags,
            onToNote = onToNote,
            onChangeTagOptions = onChangeTagOptions,
            modifier = screenModifier,
        )
    }
}

@Composable
fun NoteListScreenContent(
    notes: List<Note>,
    tags: List<String>,
    onToNote: (String) -> Unit,
    onChangeTagOptions: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        TagFilter(
            tags = tags,
            onChangeTagOptions = onChangeTagOptions,
        )
    }
}

@Composable
fun TagFilter(
    tags: List<String>,
    onChangeTagOptions: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ) {
        for (tag in tags) {
            TagPill(
                tag = tag,
                selected = true,
                onTagPillClick = { /* TODO */ },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

