package com.example.ideawhirl.ui.note

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorColors
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults.richTextEditorColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = rememberRichTextState()
    val isEditing by rememberSaveable { mutableStateOf(true) }

    state.setConfig(
        linkColor = Color.Blue,
        linkTextDecoration = TextDecoration.Underline,
    )

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedRichTextEditor(
            state = state,
            readOnly = !isEditing,
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .padding(8.dp),
            colors = richTextEditorColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        )
        RTEControls(
            state = state,
            isEditing = isEditing,
            modifier = modifier.fillMaxWidth()
        )
        TestButton()
    }
}

@Composable
fun TestButton() {
    var active by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
                .height(64.dp)
                .align(Alignment.TopCenter)
                .clip(MaterialTheme.shapes.large)
                .background(Color.LightGray),
            horizontalArrangement = Arrangement.End,
        ) {
            Row(
                modifier = Modifier.animateContentSize(),
            ) {
                if (active) {
                    IconButton(
                        onClick = { active = !active },
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                        )
                    }

                    IconButton(
                        onClick = { active = !active },
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                        )
                    }
                    IconButton(
                        onClick = { active = !active },
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                        )
                    }
                    IconButton(
                        onClick = { active = !active },
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { active = !active },
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .width(28.dp)
                                .height(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RTEControls(
    state: RichTextState,
    isEditing: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        RTEControlButton(
            selected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
            onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) },
            iconId = Icons.Filled.FormatBold,
            description = "Bold"
        )
        RTEControlButton(
            selected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
            onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) },
            iconId = Icons.Filled.FormatItalic,
            description = "Italic"
        )
        RTEControlButton(
            selected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
            onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) },
            iconId = Icons.Filled.FormatUnderlined,
            description = "Underline"
        )
        RTEControlButton(
            selected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
            onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) },
            iconId = Icons.Filled.FormatStrikethrough,
            description = "Strikethrough"
        )
        RTEControlButton(
            selected = state.isUnorderedList,
            onClick = { state.toggleUnorderedList() },
            iconId = Icons.Filled.FormatListBulleted,
            description = "Unordered List"
        )
        RTEControlButton(
            selected = state.isOrderedList,
            onClick = { state.toggleOrderedList() },
            iconId = Icons.Filled.FormatListNumbered,
            description = "Ordered List"
        )
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
    IconButton(
        onClick = onClick,
        modifier = modifier
            .width(48.dp)
            .height(48.dp)
            .clip(MaterialTheme.shapes.small),
        colors = IconButtonDefaults.iconButtonColors(
        containerColor = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surface
        },
    ),
    ) {
        Icon(
            imageVector = iconId,
            contentDescription = description,
            modifier = Modifier
                .width(28.dp)
                .height(28.dp)
        )
    }
}

@Preview
@Composable
fun RTEControlButtonPreview() {
    IdeaWhirlTheme {
        RTEControlButton(
            selected = true,
            onClick = { /*TODO*/ },
            iconId = Icons.Filled.FormatBold,
            description = ""
        )
    }
}