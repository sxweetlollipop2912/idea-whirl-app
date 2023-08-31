package com.example.ideawhirl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun ThisNavGraph(
    modifier: Modifier = Modifier,
    repository: NoteRepo,
    thisNavController: ThisNavController,
    startDestination: String,
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
            )
        }
        composable(
            route =
            NavRoutes.NOTE.route +
                    "/{${NavRoutes.NOTE.args[0]}}",
            arguments = listOf(
                navArgument(NavRoutes.NOTE.args[0]) {
                    type = NavType.StringType
                },
            )
        ) { navBackStackEntry ->
            val arguments = requireNotNull(navBackStackEntry.arguments)
            val id = requireNotNull(arguments.getString(NavRoutes.NOTE.args[0]))

            val noteViewModel: NoteViewModel = viewModel(
                factory = NoteViewModel.provideFactory(
                    repository = repository,
                    id = id,
                )
            )
            NoteRoute(
                noteViewModel = noteViewModel,
                id = id,
            ) { thisNavController.popBackStack() }
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

@Composable
fun NoteListRoute(noteListViewModel: NoteListViewModel, onToNote: (String) -> Unit, onBack: () -> Unit) {}

@Composable
fun NoteRoute(noteViewModel: NoteViewModel, id: String, onBack: () -> Unit) {}

@Composable
fun HomeRoute(homeViewModel: HomeViewModel, onToNote: (String) -> Unit, onToNoteList: () -> Unit, onToSettings: () -> Unit) {}

class NoteRepo

class HomeViewModel(
    private val repository: NoteRepo
) : ViewModel() {
    companion object {
        fun provideFactory(
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repository) as T
            }
        }
    }
}

class NoteViewModel(
    private val repository: NoteRepo,
    private val id: String,
) : ViewModel() {
    companion object {
        fun provideFactory(
            repository: NoteRepo,
            id: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteViewModel(repository, id) as T
            }
        }
    }
}

class NoteListViewModel(
    private val repository: NoteRepo,
) : ViewModel() {
    companion object {
        fun provideFactory(
            repository: NoteRepo,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NoteListViewModel(repository) as T
            }
        }
    }
}

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