package org.nosemaj.rickmorty.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.nosemaj.rickmorty.ui.details.CharacterDetailScreen
import org.nosemaj.rickmorty.ui.list.CharacterListScreen

@Composable
fun RickAndMortApp() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            CharacterListScreen { characterId ->
                navController.navigate("detail/${characterId}")
            }
        }
        composable(
            route = "detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType }),
        ) {
            CharacterDetailScreen {
                navController.navigateUp()
            }
        }
    }
}
