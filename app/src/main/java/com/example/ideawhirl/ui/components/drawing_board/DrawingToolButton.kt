package com.example.ideawhirl.ui.components.drawing_board

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DrawingToolButton(
    onClick: () -> Unit,
    backgroundColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(42.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = backgroundColor,
        ),
    ) {
        content()
    }
}