package com.example.ideawhirl.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme

@Composable
fun TagPill(
    tag: String,
    selected: Boolean,
    onTagPillClick: () -> Unit,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    notSelectedContainerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    notSelectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = MaterialTheme.shapes.small,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
        shape = shape,
        onClick = onTagPillClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) {
                selectedContainerColor
            } else {
                notSelectedContainerColor
            },
            contentColor = if (selected) {
                selectedContentColor
            } else {
                notSelectedContentColor
            },
        ),
    ) {
        Text(
            text = tag,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 2.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Preview
@Composable
fun PreviewTagPill() {
    IdeaWhirlTheme {
        TagPill(
            tag = "tag1",
            selected = true,
            onTagPillClick = { /* TODO */ },
        )
    }
}