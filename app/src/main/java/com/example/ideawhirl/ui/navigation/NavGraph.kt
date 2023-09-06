package com.example.ideawhirl.ui.navigation

import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.ideawhirl.ui.noteDrawdraw.NoteDrawViewModel
import com.example.ideawhirl.ui.notedraw.NoteDrawRoute
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
                onToCreateNote = { thisNavController.navigateToNote(navBackStackEntry, -1) },
                onToNoteList = { thisNavController.navigateTo(NavRoutes.NOTE_LIST) },
                sensorManager = sensorManager
            )
        }

        var route = NavRoutes.NOTE.route + "?"
        for (arg in NavRoutes.NOTE.args) {
            route += "$arg={$arg}"
            if (arg != NavRoutes.NOTE.args.last()) {
                route += "&"
            }
        }

        composable(
            route = route,
            arguments = listOf(
                navArgument(NavRoutes.NOTE.args[0]) {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument(NavRoutes.NOTE.args[1]) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val id = arguments.getInt(NavRoutes.NOTE.args[0])
            val isDrawing = arguments.getBoolean(NavRoutes.NOTE.args[1])

            if (!isDrawing) {
                val noteViewModel: NoteViewModel = viewModel(
                    factory = NoteViewModel.provideFactory(
                        noteId = id,
                        repository = repository,
                    )
                )
                NoteRoute(
                    noteViewModel = noteViewModel,
                    onToNoteDraw = { thisNavController.navigateToNoteDraw(navBackStackEntry, id) },
                    onBack = { thisNavController.popBackStack() }
                )
            } else {
                val noteDrawViewModel: NoteDrawViewModel = viewModel(
                    factory = NoteDrawViewModel.provideFactory(
                        noteId = id,
                        repository = repository,
                    )
                )
                NoteDrawRoute(
                    noteDrawViewModel = noteDrawViewModel,
                    onBack = { thisNavController.popBackStack() }
                )
            }
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
                onToCreateNote = { thisNavController.navigateToNote(navBackStackEntry, -1) },
                onBack = { thisNavController.popBackStack() }
            )
        }
    }
}