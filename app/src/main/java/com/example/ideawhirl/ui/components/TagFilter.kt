package com.example.ideawhirl.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme

@Composable
fun TagFilter(
    tags: List<String>,
    selectedTags: List<String>,
    onSelectAllTags: () -> Unit,
    onAddTagOption: (String) -> Unit,
    onRemoveTagOption: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spaceBetweenTags = 8.dp
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spaceBetweenTags),
    ) {
        key("") {
            if (selectedTags.isNotEmpty()) {
                TagPill(
                    tag = "All",
                    selected = false,
                    onTagPillClick = onSelectAllTags,
                    modifier = Modifier.padding(start = spaceBetweenTags),
                )
            } else {
                TagPill(
                    tag = "All",
                    selected = true,
                    onTagPillClick = onSelectAllTags,
                    modifier = Modifier.padding(start = spaceBetweenTags),
                )
            }
        }
        for (tag in tags) {
            key(tag) {
                if (tag in selectedTags) {
                    TagPill(
                        tag = tag,
                        selected = true,
                        onTagPillClick = { onRemoveTagOption(tag) },
                    )
                } else {
                    TagPill(
                        tag = tag,
                        selected = false,
                        onTagPillClick = { onAddTagOption(tag) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTagFilter() {
    IdeaWhirlTheme {
        TagFilter(
            tags = listOf("tag1", "tag2", "tag3"),
            selectedTags = listOf("tag1", "tag2"),
            onSelectAllTags = { /* TODO */ },
            onAddTagOption = { /* TODO */ },
            onRemoveTagOption = { /* TODO */ },
        )
    }
}