package com.example.ideawhirl.ui.home

import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    onToNote: (Int) -> Unit,
    onToNoteList: () -> Unit,
    onToSettings: () -> Unit,
    sensorManager: SensorManager,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) { homeViewModel.getAllNotes() }
    HomeScreen(
        onToNote = onToNote,
        onToNoteList = onToNoteList,
        onToSettings = onToSettings,
        sensorManager = sensorManager,
        tags = arrayOf("Study", "Drafts", "Ideas"), // TODO: get tags from database
        getRandomNote = { homeViewModel.getRandomNote() },
        getRandomNoteWithTag = { tag -> homeViewModel.getRandomNoteWithTag(tag) },
        modifier = modifier,
    )
}