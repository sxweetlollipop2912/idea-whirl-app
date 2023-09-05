package com.example.ideawhirl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// 4 routes: home, notelist, note, settings
enum class NavRoutes(val route: String, val args: List<String>) {
    HOME("home", emptyList()),
    NOTE_LIST("note_list", emptyList()),
    NOTE("note", listOf("id")),
    SETTINGS("settings", emptyList()),
}

@Composable
fun rememberThisNavController(
    navController: NavHostController = rememberNavController()
): ThisNavController = remember(navController) {
    ThisNavController(navController)
}

@Stable
class ThisNavController(
    val navController: NavHostController,
) {
    private val currentRoute: String?
        get() = navController.currentDestination?.route

    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigateTo(destination: NavRoutes) {
        val route = destination.route
        if (route != currentRoute) {
            navController.navigate(route) {
                // Avoid multiple copies of the same destination when
                // re-selecting the same item
                launchSingleTop = true
                // Restore state when re-selecting a previously selected item
                restoreState = true
            }
        }
    }

    fun navigateToNote(
        from: NavBackStackEntry,
        id: String,
    ) {
        val url = "${NavRoutes.NOTE.route}/$id"
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            navController.navigate(url)
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED