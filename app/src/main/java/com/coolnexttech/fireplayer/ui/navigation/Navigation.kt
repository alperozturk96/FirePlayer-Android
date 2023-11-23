package com.coolnexttech.fireplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coolnexttech.fireplayer.view.HomeView
import com.coolnexttech.fireplayer.viewModel.HomeViewModel

@Composable
fun Navigation(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination) {
        composable(route = Destinations.Home) {
            val viewModel: HomeViewModel = viewModel()
            HomeView(navController, viewModel)
        }
    }
}