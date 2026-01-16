package com.example.topstackoverusers.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.topstackoverusers.ui.screens.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination

fun NavGraphBuilder.homeScreen(navController: NavController) {

    composable<HomeDestination> {
        HomeScreen()
    }
}