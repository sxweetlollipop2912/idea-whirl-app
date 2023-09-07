package com.example.ideawhirl.ui.note

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.Gesture
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.model.Note
import com.example.ideawhirl.ui.components.ColorPickerPopup
import com.example.ideawhirl.ui.components.TagListWithAdd
import com.example.ideawhirl.ui.components.TagPill
import com.example.ideawhirl.ui.theme.NotePalette
import com.example.ideawhirl.ui.theme.NoteTheme
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalRichTextApi::class)
@Composable
fun NoteScreen(
    note: Note,
    globalTags: List<String>,
    uiState: NoteUiState,
    onRequestNoteEdit: () -> Unit,
    onRequestTitleEdit: () -> Unit,
    onTitleSubmit: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onDoneEditing: () -> Unit,
    onToNoteDraw: () -> Unit,
    onBack: () -> Unit,
    onTagAdded: (String) -> Unit,
    onTagUpdated: (String, String) -> Unit,
    onTagRemoved: (String) -> Unit,
    onPaletteChanged: (NotePalette) -> Unit,
    modifier: Modifier = Modifier,
) {
    NoteTheme(palette = note.palette) {
        val isInEditMode = uiState.isInEditMode
        val isEditingTitle = uiState.isEditingTitle
        val richTextState = rememberRichTextState()
        richTextState.setConfig(
            linkColor = MaterialTheme.colorScheme.inversePrimary,
            linkTextDecoration = TextDecoration.Underline,
        )
        // load note content from viewmodel
        if (!isInEditMode) {
            richTextState.setMarkdown(note.detail)
        }

        val focusManager = LocalFocusManager.current
        var requestingFocusRTE by rememberSaveable { mutableStateOf(uiState.isEditingContent) }
        val focusRequesterRTE = remember { FocusRequester() }
        if (requestingFocusRTE) {
            requestingFocusRTE = false
            LaunchedEffect(Unit) {
                Log.d("NoteScreen", "requesting focus")
                focusRequesterRTE.requestFocus()
            }
        }

        val focusRequesterTitle = remember { FocusRequester() }

        var bottomBarExpanded by rememberSaveable { mutableStateOf(false) }

        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = {
                        Row {
                            if (isEditingTitle) {
                                EditField(
                                    content = note.name,
                                    onChanged = onTitleChanged,
                                    onSubmit = onTitleSubmit,
                                    focusRequester = focusRequesterTitle,
                                )
                            } else {
                                DisplayField(
                                    isInEditMode = isInEditMode,
                                    onEditClick = onRequestTitleEdit
                                ) {
                                    Text(
                                        text = note.name,
                                        style = MaterialTheme.typography.headlineLarge.copy(
                                            color = MaterialTheme.colorScheme.onBackground,
                                            platformStyle = PlatformTextStyle(
                                                includeFontPadding = false,
                                            ),
                                        )
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Outlined.ArrowBackIosNew,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (isInEditMode) {
                            val palettesMain = NotePalette.values().map { Pair(it, it.main) }
                            Box(
                                modifier = modifier
                                    .width(100.dp)
                                    .wrapContentHeight(),
                            ) {
                                ColorPickerPopup(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    colors = palettesMain.map { it.second },
                                    onColorSelected = { selected ->
                                        onPaletteChanged(
                                            palettesMain.find { it.second == selected }?.first
                                                ?: NotePalette.PALETTE_0
                                        )
                                    },
                                    backgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(
                                        alpha = 0.4f
                                    ),
                                )
                                FloatingActionButton(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .align(Alignment.TopEnd),
                                    onClick = {
                                        onContentChanged(richTextState.toMarkdown())
                                        onDoneEditing()
                                        focusManager.clearFocus()
                                    },
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Done,
                                        contentDescription = "save note",
                                    )
                                }
                            }
                        } else {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                FloatingActionButton(
                                    modifier = Modifier.size(42.dp),
                                    onClick = {
                                        onContentChanged(richTextState.toMarkdown())
                                        onDoneEditing()
                                        onToNoteDraw()
                                    },
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Gesture,
                                        contentDescription = "go to drawing",
                                    )
                                }
                                FloatingActionButton(
                                    modifier = Modifier.size(42.dp),
                                    onClick = {
                                        onRequestNoteEdit()
                                        requestingFocusRTE = true
                                        bottomBarExpanded = true
                                    },
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EditNote,
                                        contentDescription = "edit note",
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                    ),
                    modifier = Modifier.padding(end = 16.dp),
                )
            },
            bottomBar = {
                if (isInEditMode) {
                    BottomBar(
                        bottomBarExpanded,
                        onToggleExpansion = {
                            bottomBarExpanded = !bottomBarExpanded
                        },
                        richTextState,
                        onDrawClick = {
                            // save note content to viewmodel
                            onContentChanged(richTextState.toMarkdown())
                            onDoneEditing()
                            onToNoteDraw()
                        }
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            val screenModifier = Modifier.padding(
                innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                0.dp,
            )
            NoteScreenContent(
                note,
                globalTags,
                uiState,
                richTextState,
                onTagAdded,
                onTagUpdated,
                onTagRemoved,
                focusRequesterRTE,
                onRichTextFocusRequest = {
                    requestingFocusRTE = true
                },
                screenModifier
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreenContent(
    note: Note,
    globalTags: List<String>,
    uiState: NoteUiState,
    richTextState: RichTextState,
    onTagAdded: (String) -> Unit,
    onTagUpdated: (String, String) -> Unit,
    onTagRemoved: (String) -> Unit,
    focusRequester: FocusRequester,
    onRichTextFocusRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInEditMode = uiState.isInEditMode
    var isAddingTag by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        TagListWithAdd(
            tags = note.tags.toList(),
            globalTags = globalTags,
            onAddClick = {
                isAddingTag = true
            },
            isEditingNote = isInEditMode,
            onTagUpdated = onTagUpdated,
            onTagRemoved = onTagRemoved,
            contentHorizontalPadding = 16.dp,
            addButtonRemove = !isInEditMode,
            modifier = Modifier.fillMaxWidth(),
        )

        val interactionSource = MutableInteractionSource()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = uiState.isEditingContent
                ) {
                    onRichTextFocusRequest()
                }
        ) {
            RichTextEditor(
                state = richTextState,
                readOnly = !isInEditMode,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                colors = richTextEditorColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    selectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colorScheme.primary,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                    ),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                contentPadding = PaddingValues(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 0.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(focusRequester),
            )
            Spacer(
                modifier = Modifier.height(300.dp)
            )
        }
    }

    var newTag by rememberSaveable { mutableStateOf("") }

    if (isAddingTag) {
        AlertDialog(
            onDismissRequest = {
                isAddingTag = false
                newTag = ""
            },
            title = {
                Text(
                    text = "Add tag",
                    style = MaterialTheme.typography.labelLarge,
                )
            },
            text = {
                Column {
                    TextField(
                        value = newTag,
                        onValueChange = {
                            newTag = it
                        },

                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            selectionColors = TextSelectionColors(
                                handleColor = MaterialTheme.colorScheme.primary,
                                backgroundColor = MaterialTheme.colorScheme.secondary,
                            )
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        for (globalTag in globalTags.filter { it.contains(newTag) && !note.tags.contains(it) }) {
                            key(globalTag) {
                                TagPill(
                                    tag = globalTag,
                                    selected = false,
                                    onTagPillClick = {
                                        newTag = globalTag
                                    },
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedContentColor = MaterialTheme.colorScheme.onSecondary,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isAddingTag = false
                        onTagAdded(newTag)
                        newTag = ""
                    },
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        isAddingTag = false
                        newTag = ""
                    },
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.inversePrimary
                    )
                }
            }
        )
    }
}

@Composable
fun BottomBar(
    expanded: Boolean,
    onToggleExpansion: () -> Unit,
    state: RichTextState,
    onDrawClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(
                    if (expanded)
                        Alignment.TopCenter
                    else
                        Alignment.TopEnd
                )
                .shadow(
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                )
                .background(MaterialTheme.colorScheme.secondary)
                .padding(5.dp)
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (expanded) {
                RTEControlButton(
                    selected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                    onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
                    iconId = Icons.Outlined.FormatBold,
                    description = "Bold"
                )
                RTEControlButton(
                    selected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                    onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
                    iconId = Icons.Outlined.FormatItalic,
                    description = "Italic"
                )
//                RTEControlButton(
//                    selected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
//                    onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
//                    iconId = Icons.Outlined.FormatUnderlined,
//                    description = "Underline"
//                )
                RTEControlButton(
                    selected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                    onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) },
                    iconId = Icons.Outlined.FormatStrikethrough,
                    description = "Strikethrough"
                )
                RTEControlButton(
                    selected = state.isUnorderedList,
                    onClick = { state.toggleUnorderedList() },
                    iconId = Icons.Outlined.FormatListBulleted,
                    description = "Unordered List"
                )
                RTEControlButton(
                    selected = state.isOrderedList,
                    onClick = { state.toggleOrderedList() },
                    iconId = Icons.Outlined.FormatListNumbered,
                    description = "Ordered List"
                )
                IconButton(
                    onClick = onDrawClick,
                    modifier = Modifier.size(42.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Gesture,
                        contentDescription = "Draw",
                        modifier = Modifier.size(28.dp),
                    )
                }
                IconButton(
                    onClick = onToggleExpansion,
                    modifier = Modifier.size(42.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close tool bar",
                        modifier = Modifier.size(28.dp),
                    )
                }
            } else {
                IconButton(
                    onClick = onToggleExpansion,
                    modifier = Modifier.size(42.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Open tool bar",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RTEControlButton(
    selected: Boolean,
    onClick: () -> Unit,
    iconId: ImageVector,
    description: String,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(42.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Transparent
        },
    ),
    ) {
        Icon(
            imageVector = iconId,
            contentDescription = description,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun DisplayField(
    isInEditMode: Boolean,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BaseField(
        rightIconDrawable = if (isInEditMode)
            Icons.Outlined.Edit
        else null,
        onRightIconClick = onEditClick,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
private fun EditField(
    content: String,
    onChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var hasFocused by rememberSaveable { mutableStateOf(false) }

    BaseField(
        rightIconDrawable = null,
        onRightIconClick = {},
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp, 5.dp)
        ) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.secondary,
            )

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                var textFieldValueState by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = content,
                            selection = TextRange(content.length)
                        )
                    )
                }
                BasicTextField(
                    value = textFieldValueState,
                    onValueChange = {
                        textFieldValueState = it
                        onChanged(it.text)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                hasFocused = true
                            }
                            if (!it.isFocused && hasFocused) {
                                onSubmit()
                            }
                        },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("NoteScreen: EditField", "requesting focus")
        focusRequester.requestFocus()
    }
}

@Composable
private fun BaseField(
    rightIconDrawable: ImageVector?,
    onRightIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentUnit: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        contentUnit()
        if (rightIconDrawable != null) {
            IconButton(
                onClick = onRightIconClick,
            ) {
                Icon(
                    imageVector = rightIconDrawable,
                    contentDescription = null,
                )
            }
        }
    }
}