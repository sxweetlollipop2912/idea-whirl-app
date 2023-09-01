package com.example.ideawhirl.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ideawhirl.ui.theme.IdeaWhirlTheme

@Composable
fun TagPill(
    tag: String,
    selected: Boolean,
    onTagPillClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        onClick = onTagPillClick,
    ) {
        Text(
            text = tag.uppercase(),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.titleMedium,
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