package com.example.ideawhirl.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.model.NotePalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagListWithAdd(
    tags: List<String>,
    globalTags: List<String>,
    onAddClick: () -> Unit,
    isEditingNote: Boolean,
    onTagUpdated: (String, String) -> Unit,
    onTagRemoved: (String) -> Unit,
    palette: NotePalette,
    contentHorizontalPadding: Dp = 0.dp,
    addButtonRemove: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val spaceBetweenTags = 8.dp
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spaceBetweenTags),
        verticalAlignment = Alignment.CenterVertically
    ) {
        key("startPadding") {
            Spacer(modifier = Modifier.width(contentHorizontalPadding - spaceBetweenTags))
        }
        if (!addButtonRemove) {
            key("") {
                FilledIconButton(
                    onClick = onAddClick,
                    shape = CircleShape,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = palette.variant,
                        contentColor = palette.onVariant
                    ),
                    modifier = Modifier
                        .size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "add tag",
                    )
                }
            }
        }
        for (tag in tags) {
            key(tag) {
                var isEditingTag by remember { mutableStateOf(false) }
                var newTag by remember { mutableStateOf(tag) }
                TagPill(
                    tag = tag,
                    selected = true,
                    onTagPillClick = {
                        if (isEditingNote) {
                            isEditingTag = true
                        }
                    },
                    selectedContainerColor = palette.variant,
                    selectedContentColor = palette.onVariant,
                )
                if (isEditingTag) {
                    AlertDialog(
                        onDismissRequest = {
                            isEditingTag = false
                            newTag = tag
                        },
                        title = {
                            Text(
                                text = "Edit tag",
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
                                        cursorColor = palette.main,
                                        focusedIndicatorColor = palette.main,
                                        selectionColors = TextSelectionColors(
                                            handleColor = palette.main,
                                            backgroundColor = palette.variant,
                                        )
                                    )
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                ) {
                                    for (globalTag in globalTags.filter { it.contains(newTag) && !tags.contains(it) }) {
                                        key(globalTag) {
                                            TagPill(
                                                tag = globalTag,
                                                selected = false,
                                                onTagPillClick = {
                                                    newTag = globalTag
                                                },
                                                selectedContainerColor = palette.variant,
                                                selectedContentColor = palette.onVariant,
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    isEditingTag = false
                                    onTagUpdated(tag, newTag)
                                },
                            ) {
                                Text(
                                    text = "OK",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.onEmphasis
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(
                               onClick = {
                                   isEditingTag = false
                                   onTagRemoved(tag)
                               }
                            ) {
                                Text(
                                    text = "Delete",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            TextButton(
                                onClick = {
                                    isEditingTag = false
                                    newTag = tag
                                },
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = palette.onEmphasis
                                )
                            }
                        }
                    )

                }
            }
        }
        key("endPadding") {
            Spacer(modifier = Modifier.width(contentHorizontalPadding - spaceBetweenTags))
        }
    }
}