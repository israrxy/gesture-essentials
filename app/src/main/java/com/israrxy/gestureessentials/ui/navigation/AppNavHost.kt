package com.israrxy.gestureessentials.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.israrxy.gestureessentials.AppGraph
import com.israrxy.gestureessentials.ui.editor.EditorRoute
import com.israrxy.gestureessentials.ui.settings.SettingsRoute

@Composable
fun AppNavHost(
    graph: AppGraph,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SETTINGS,
        modifier = modifier,
    ) {
        // Older saved back stacks may still contain Home or Rules after an update.
        listOf(Routes.SETTINGS, Routes.MORE_SETTINGS, Routes.HOME, Routes.RULES).forEach { route ->
            composable(route) {
                SettingsRoute(
                    graph = graph,
                    showMore = route == Routes.MORE_SETTINGS,
                    onBack = {
                        if (route == Routes.MORE_SETTINGS) navController.popBackStack() else onFinish()
                    },
                    onNewRule = {
                        navController.navigate(Routes.editor(null)) { launchSingleTop = true }
                    },
                    onEditRule = { id ->
                        navController.navigate(Routes.editor(id)) { launchSingleTop = true }
                    },
                    onMoreSettings = {
                        navController.navigate(Routes.MORE_SETTINGS) { launchSingleTop = true }
                    },
                )
            }
        }

        composable(
            route = Routes.EDITOR_PATTERN,
            arguments = listOf(
                navArgument("ruleId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
            ),
        ) { backStackEntry ->
            EditorRoute(
                graph = graph,
                ruleId = backStackEntry.arguments?.getString("ruleId"),
                onBack = { navController.popBackStack() },
            )
        }
    }
}
