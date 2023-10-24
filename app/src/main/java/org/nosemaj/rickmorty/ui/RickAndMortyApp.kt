package org.nosemaj.rickmorty.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
        ) {navBackStackEntry ->
            navBackStackEntry.arguments?.getInt("characterId")?.let { characterId ->
                CharacterDetailScreen(characterId) {
                    navController.navigateUp()
                }
            }
        }
    }
}
