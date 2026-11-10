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
import com.israrxy.gestureessentials.ui.home.HomeRoute
import com.israrxy.gestureessentials.ui.rules.RulesRoute

@Composable
fun AppNavHost(
    graph: AppGraph,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeRoute(
                graph = graph,
                onOpenRules = { navController.navigate(Routes.RULES) },
            )
        }

        composable(Routes.RULES) {
            RulesRoute(
                graph = graph,
                onBack = { navController.popBackStack() },
                onNewRule = { navController.navigate(Routes.editor(null)) },
                onOpenRule = { ruleId -> navController.navigate(Routes.editor(ruleId)) },
            )
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
