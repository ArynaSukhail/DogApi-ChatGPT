package com.example.myapplication.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.Screen
import com.example.myapplication.ui.screens.BreedDetailScreen
import com.example.myapplication.ui.screens.BreedListScreen
import com.example.myapplication.ui.screens.MainViewModel

@Composable
fun DogApp(navController: NavHostController = rememberNavController()) {
    val viewModel: MainViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    NavHost(navController, startDestination = Screen.List.route) {
        composable(Screen.List.route) {
            BreedListScreen(
                uiState,
                onItemClick = {
                    navController.navigate(Screen.Detail.createRoute(it.id))
                },
                onLoadMore = { viewModel.loadNextPage() })
        }
        composable(Screen.Detail.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            val breed = uiState.breeds.find { it.id == id }
            if (breed != null) {
                BreedDetailScreen(
                    breed = breed,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                Text("Порода не найдена")
            }
        }
    }
}