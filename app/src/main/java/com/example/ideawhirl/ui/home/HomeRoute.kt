package com.example.ideawhirl.ui.home

import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeRoute(
    homeViewModel: HomeViewModel,
    onToNote: (String) -> Unit,
    onToNoteList: () -> Unit,
    onToSettings: () -> Unit,
    sensorManager: SensorManager,
    modifier: Modifier = Modifier,
) {
    HomeScreen(
        onToNote = onToNote,
        onToNoteList = onToNoteList,
        onToSettings = onToSettings,
        sensorManager = sensorManager,
        onShake = { homeViewModel.getRandomNote() },
        onStopDrag = { homeViewModel.getRandomNote() },
        modifier = modifier,
    )
}