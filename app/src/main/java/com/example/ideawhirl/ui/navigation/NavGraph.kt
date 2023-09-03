package com.example.ideawhirl.ui.navigation

import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ideawhirl.data.repo.NoteRepo
import com.example.ideawhirl.ui.home.HomeRoute
import com.example.ideawhirl.ui.home.HomeViewModel
import com.example.ideawhirl.ui.note.NoteRoute
import com.example.ideawhirl.ui.note.NoteViewModel
import com.example.ideawhirl.ui.notelist.NoteListRoute
import com.example.ideawhirl.ui.notelist.NoteListViewModel

@Composable
fun ThisNavGraph(
    modifier: Modifier = Modifier,
    repository: NoteRepo,
    thisNavController: ThisNavController,
    startDestination: String,
    sensorManager: SensorManager
) {
    NavHost(
        navController = thisNavController.navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME.route) { navBackStackEntry ->
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.provideFactory(
                    repository = repository,
                )
            )
            HomeRoute(
                homeViewModel = homeViewModel,
                onToNote = { id -> thisNavController.navigateToNote(navBackStackEntry, id) },
                onToNoteList = { thisNavController.navigateTo(NavRoutes.NOTE_LIST) },
                onToSettings = { thisNavController.navigateTo(NavRoutes.SETTINGS) },
                sensorManager = sensorManager
            )
        }
        composable(
            route =
            NavRoutes.NOTE.route +
                    "/{${NavRoutes.NOTE.args[0]}}",
            arguments = listOf(
                navArgument(NavRoutes.NOTE.args[0]) {
                    type = NavType.IntType
                    defaultValue = -1
                },
            )
        ) { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val id = arguments.getInt(NavRoutes.NOTE.args[0])

            val noteViewModel: NoteViewModel = viewModel(
                factory = NoteViewModel.provideFactory(
                    repository = repository,
                    noteId = id,
                    editingAsFirstState = false,
                )
            )
            NoteRoute(
                noteViewModel = noteViewModel,
                noteId = id,
                onBack = { thisNavController.popBackStack() }
            )
        }
        composable(NavRoutes.NOTE_LIST.route) { navBackStackEntry ->
            val noteListViewModel: NoteListViewModel = viewModel(
                factory = NoteListViewModel.provideFactory(
                    repository = repository,
                )
            )
            NoteListRoute(
                noteListViewModel = noteListViewModel,
                onToNote = { id -> thisNavController.navigateToNote(navBackStackEntry, id) },
                onToCreateNote = { thisNavController.navigateTo(NavRoutes.NOTE) },
                onBack = { thisNavController.popBackStack() }
            )
        }
        composable(NavRoutes.SETTINGS.route) {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModel.provideFactory(
                    repository = repository,
                )
            )
            SettingsRoute(
                settingsViewModel = settingsViewModel,
                onBack = { thisNavController.popBackStack() },
            )
        }
    }
}

@Composable
fun SettingsRoute(settingsViewModel: SettingsViewModel, onBack: () -> Unit) {}


class SettingsViewModel(
    private val repository: NoteRepo,
) : ViewModel() {
    companion object {
        fun provideFactory(
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(repository) as T
            }
        }
    }
}