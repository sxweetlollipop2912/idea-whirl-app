package com.example.ideawhirl.ui.note

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material.icons.outlined.FormatItalic
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.FormatStrikethrough
import androidx.compose.material.icons.outlined.FormatUnderlined
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.ideawhirl.model.NotePalette
import com.example.ideawhirl.ui.components.TagListWithAdd
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalRichTextApi::class)
@Composable
fun NoteScreen(
    note: Note,
    uiState: NoteUiState,
    onRequestTitleEdit: () -> Unit,
    onTitleSubmit: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onDoneEditing: () -> Unit,
    onBack: () -> Unit,
    onRequestToAddNewTags: () -> Unit,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInEditMode = uiState.isInEditMode
    val isEditingTitle = uiState.isEditingTitle
    val richTextState = rememberRichTextState()

    richTextState.setConfig(
        // TODO: set color that matches current palette
        linkColor = note.palette.main,
        linkTextDecoration = TextDecoration.Underline,
    )

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
                                palette = note.palette,
                            )
                        } else {
                            DisplayField(
                                isInEditMode = isInEditMode,
                                onEditClick = onRequestTitleEdit
                            ) {
                                Text(
                                    text = note.name,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        color = note.palette.onBackground,
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
                        FloatingActionButton(
                            modifier = Modifier.size(42.dp),
                            onClick = onDoneEditing,
                            shape = CircleShape,
                            containerColor = note.palette.variant,
                            contentColor = note.palette.onVariant,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Done,
                                contentDescription = "save note",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = note.palette.background,
                ),
                modifier = Modifier.padding(end = 16.dp),
            )
        },
        bottomBar = {
            if (isInEditMode) {
                BottomBar(richTextState, note.palette)
            }
        },
        containerColor = note.palette.background,
    ) { innerPadding ->
        val screenModifier = Modifier.padding(
            innerPadding.calculateStartPadding(LayoutDirection.Ltr),
            innerPadding.calculateTopPadding(),
            innerPadding.calculateEndPadding(LayoutDirection.Ltr),
            0.dp,
        )
        NoteScreenContent(
            note,
            uiState,
            richTextState,
            onRequestToAddNewTags,
            onTagClick,
            screenModifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreenContent(
    note: Note,
    uiState: NoteUiState,
    richTextState: RichTextState,
    onRequestToAddNewTags: () -> Unit,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isEditing = uiState.isEditingContent

    Column(modifier = modifier.fillMaxSize()) {
        TagListWithAdd(
            tags = note.tags,
            onAddClick = onRequestToAddNewTags,
            onTagClick = onTagClick,
            palette = note.palette,
            contentHorizontalPadding = 16.dp,
            modifier = Modifier.fillMaxWidth(),
        )
        RichTextEditor(
            state = richTextState,
            readOnly = !isEditing,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = note.palette.onBackground
            ),
            colors = richTextEditorColors(
                containerColor = note.palette.background,
                cursorColor = note.palette.main,
                selectionColors = TextSelectionColors(
                    handleColor = note.palette.main,
                    backgroundColor = note.palette.variant,
                ),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            contentPadding = PaddingValues(
                top = 0.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 0.dp
            ),
            modifier = modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}

@Composable
fun BottomBar(
    state: RichTextState,
    palette: NotePalette,
    modifier: Modifier = Modifier,
) {
    var active by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .align(
                    if (active)
                        Alignment.TopCenter
                    else
                        Alignment.TopEnd
                )
                .shadow(
                    elevation = 2.dp,
                    shape = MaterialTheme.shapes.small,
                )
                .background(palette.variant)
                .padding(5.dp)
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (active) {
                RTEControlButton(
                    selected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
                    onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
                    iconId = Icons.Outlined.FormatBold,
                    palette = palette,
                    description = "Bold"
                )
                RTEControlButton(
                    selected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
                    onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
                    iconId = Icons.Outlined.FormatItalic,
                    palette = palette,
                    description = "Italic"
                )
                RTEControlButton(
                    selected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
                    onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
                    iconId = Icons.Outlined.FormatUnderlined,
                    palette = palette,
                    description = "Underline"
                )
                RTEControlButton(
                    selected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
                    onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) },
                    iconId = Icons.Outlined.FormatStrikethrough,
                    palette = palette,
                    description = "Strikethrough"
                )
                RTEControlButton(
                    selected = state.isUnorderedList,
                    onClick = { state.toggleUnorderedList() },
                    iconId = Icons.Outlined.FormatListBulleted,
                    palette = palette,
                    description = "Unordered List"
                )
                RTEControlButton(
                    selected = state.isOrderedList,
                    onClick = { state.toggleOrderedList() },
                    iconId = Icons.Outlined.FormatListNumbered,
                    palette = palette,
                    description = "Ordered List"
                )
                IconButton(
                    onClick = { active = !active },
                    modifier = Modifier.size(42.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = palette.onVariant,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Edit",
                        modifier = Modifier.size(28.dp),
                    )
                }
            } else {
                IconButton(
                    onClick = { active = !active },
                    modifier = Modifier.size(42.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = palette.onVariant,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
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
    palette: NotePalette,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(42.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = palette.onVariant,
            containerColor = if (selected) {
                palette.main
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
    palette: NotePalette,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var hasFocused by rememberSaveable { mutableStateOf(false) }

    BaseField(
        rightIconDrawable = null,
        onRightIconClick = {},
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(palette.background)
                .padding(10.dp, 5.dp)
        ) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = palette.main,
                backgroundColor = palette.variant,
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
                        color = palette.onBackground
                    ),
                    cursorBrush = SolidColor(palette.main)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
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