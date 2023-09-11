package com.example.ideawhirl.ui.notelist

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.components.TagFilter
import com.example.ideawhirl.ui.components.drawing_board.PreviewDisplayBoard
import com.example.ideawhirl.ui.formatDate
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    availableStrokeColors: List<Color>,
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
                            imageVector = Icons.Outlined.ArrowBackIosNew,
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
        val screenModifier = Modifier.padding(
            innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            innerPadding.calculateTopPadding(),
            innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            0.dp,
        )
        NoteListScreenContent(
            notes = notes,
            availableStrokeColors = availableStrokeColors,
            tags = tags,
            selectedTags = selectedTags,
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
    availableStrokeColors: List<Color>,
    tags: List<String>,
    selectedTags: List<String>,
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
            availableStrokeColors = availableStrokeColors,
            onDeleteNote = onDeleteNote,
            onToNote = onToNote,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
        )
    }
}

@Composable
fun NoteList(
    notes: List<Note>,
    availableStrokeColors: List<Color>,
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
                availableStrokeColors = availableStrokeColors,
                onDeleteNote = onDeleteNote,
                onItemClick = { onToNote(notes[index].uid) },
                modifier = Modifier.height(200.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListItem(
    note: Note,
    availableStrokeColors: List<Color>,
    onDeleteNote: (Note) -> Unit,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = note.palette.main
    var isMenuOpened by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box {
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
                    Text(
                        note.name, style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (note.drawingData.isEmpty()) {
                        NoteListItemPreview(
                            note = note,
                            onItemClick = onItemClick,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Column(
                            modifier = Modifier.weight(1f).padding(bottom = 8.dp),
                        ) {
                            NoteListItemPreview(
                                note = note,
                                onItemClick = onItemClick,
                                modifier = Modifier.weight(1f),
                            )
                            PreviewDisplayBoard(
                                drawingData = note.drawingData,
                                availableStrokeColors = availableStrokeColors,
                                backgroundColor = note.palette.background,
                                height = 60.dp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        formatDate(note.updatedAt!!),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            IconButton(
                onClick = { isMenuOpened = !isMenuOpened },
                modifier = Modifier.padding(start = 8.dp),

                ) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (isMenuOpened) {
                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(extraSmall = MaterialTheme.shapes.medium)
                ) {
                    DropdownMenu(
                        expanded = isMenuOpened,
                        onDismissRequest = { isMenuOpened = false },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.error
                                    )
                                )
                            },
                            onClick = {
                                onDeleteNote.invoke(note)
                                isMenuOpened = false
                            },
                            modifier = Modifier.wrapContentSize()
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Share",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            },
                            onClick = {
                                val content = "# ${note.name}\n\n${note.detail}"
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, content)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)

                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.wrapContentSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteListItemPreview(
    note: Note,
    onItemClick: () -> Unit,
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
            onClick = onItemClick,
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
            availableStrokeColors = listOf(Color.Red, Color.Blue),
        )
    }
}