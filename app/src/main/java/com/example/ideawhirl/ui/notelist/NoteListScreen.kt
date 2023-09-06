package com.example.ideawhirl.ui.notelist

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.components.TagFilter
import com.example.ideawhirl.ui.formatDate
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    tags: List<String>,
    selectedTags: List<String>,

    onDeleteNote: (Note) -> Unit,
    onToCreateNote: () -> Unit,

    onAddTagOption: (String) -> Unit,
    onRemoveTagOption: (String) -> Unit,
    onSelectAllTags: () -> Unit,

    onToNote: (Int) -> Unit,
    onBack: () -> Unit,

    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Notes")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onToCreateNote,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.small,
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "create new note")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        NoteListScreenContent(
            notes = notes,
            tags = tags,
            selectedTags = selectedTags,
            onToCreateNote = onToCreateNote,
            onDeleteNote = onDeleteNote,
            onAddTagOption = onAddTagOption,
            onRemoveTagOption = onRemoveTagOption,
            onSelectAllTags = onSelectAllTags,
            onToNote = onToNote,
            modifier = screenModifier,
        )
    }
}

@Composable
fun NoteListScreenContent(
    notes: List<Note>,
    tags: List<String>,
    selectedTags: List<String>,
    onToCreateNote: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onAddTagOption: (String) -> Unit,
    onRemoveTagOption: (String) -> Unit,
    onSelectAllTags: () -> Unit,
    onToNote: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        TagFilter(
            tags = tags,
            selectedTags = selectedTags,
            onSelectAllTags = onSelectAllTags,
            onAddTagOption = onAddTagOption,
            onRemoveTagOption = onRemoveTagOption,
            modifier = Modifier.fillMaxWidth(),
        )
        NoteList(
            notes = notes,
            onDeleteNote = onDeleteNote,
            onToNote = onToNote,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteList(
    notes: List<Note>,
    onDeleteNote: (Note) -> Unit,
    onToNote: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
    ) {
        items(
            count = notes.size,
            key = { index -> notes[index].uid }
        ) { index ->
            NoteListItem(
                note = notes[index],
                onDeleteNote = onDeleteNote,
                onItemClick = { onToNote(notes[index].uid) },
                modifier = Modifier.height(if (index == 0 || index == notes.size - 1) 150.dp else 200.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListItem(
    note: Note,
    onDeleteNote: (Note) -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = note.palette.main
    Card(
        modifier = modifier
            .fillMaxWidth(),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        onClick = onItemClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.5.dp,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            Divider(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight(),
                color = borderColor,
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(note.name, style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ))
                NoteListItemPreview(
                    note = note,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    formatDate(note.createdAt!!),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListItemPreview(
    note: Note,
    modifier: Modifier = Modifier,
) {

    // replace all '\n' with "  \n" to make sure that the line breaks are rendered
    val detail = note.detail.replace("\n", "  \n")

    Row(
        modifier = modifier,
    ) {
        MarkdownText(
            markdown = detail,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        )
    }
}

@Preview
@Composable
fun PreviewNoteListItem() {
    IdeaWhirlTheme {
        NoteListItem(
            note = Note(
                name = "test note",
                detail = "test detail",
                tags = setOf("tag 1", "tag 2")
            ),
            onDeleteNote = {},
            onItemClick = {},
        )
    }
}