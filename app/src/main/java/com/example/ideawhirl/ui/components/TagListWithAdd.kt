package com.example.ideawhirl.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.model.NotePalette

@Composable
fun TagListWithAdd(
    tags: List<String>,
    onAddClick: () -> Unit,
    onTagClick: (String) -> Unit,
    palette: NotePalette,
    contentHorizontalPadding: Dp = 0.dp,
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
            Spacer(modifier = Modifier.size(contentHorizontalPadding - spaceBetweenTags))
        }
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
        for (tag in tags) {
            key(tag) {
                TagPill(
                    tag = tag,
                    selected = true,
                    onTagPillClick = { onTagClick(tag) },
                    selectedContainerColor = palette.variant,
                    selectedContentColor = palette.onVariant,
                )
            }
        }
        key("endPadding") {
            Spacer(modifier = Modifier.size(contentHorizontalPadding - spaceBetweenTags))
        }
    }
}