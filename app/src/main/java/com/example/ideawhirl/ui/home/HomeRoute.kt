package com.example.ideawhirl.ui.home

import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    onToNote: (Int) -> Unit,
    onToCreateNote: () -> Unit,
    onToNoteList: () -> Unit,
    sensorManager: SensorManager,
    modifier: Modifier = Modifier,
) {
    val tags by homeViewModel.tags.collectAsStateWithLifecycle()
    val selectedTags by homeViewModel.selectedTags.collectAsStateWithLifecycle()
    val notes by homeViewModel.notes.collectAsStateWithLifecycle()

    HomeScreen(
        notes = notes,
        onToNote = onToNote,
        onToCreateNote = onToCreateNote,
        onToNoteList = onToNoteList,
        sensorManager = sensorManager,
        tags = tags, // TODO: get tags from database
        selectedTags = selectedTags,
        getRandomNote = { homeViewModel.getRandomNote() },
        onSelectAllTags = homeViewModel::selectAllTags,
        onAddTagOption = homeViewModel::addTagOption,
        onRemoveTagOption = homeViewModel::removeTagOption,
        modifier = modifier,
    )
}