package com.coolnexttech.fireplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coolnexttech.fireplayer.view.HomeView
import com.coolnexttech.fireplayer.view.PlaylistsView
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel
import com.coolnexttech.fireplayer.viewModel.HomeViewModel
import com.coolnexttech.fireplayer.viewModel.PlaylistsViewModel
import com.coolnexttech.fireplayer.viewModel.ViewModelProvider

@Composable
fun Navigation(navController: NavHostController, startDestination: String) {
    NavHost(navController, startDestination) {
        composable(Destinations.Home) {
            val viewModel: HomeViewModel = ViewModelProvider.getHomeViewModel()
            val audioPlayerViewModel: AudioPlayerViewModel =
                ViewModelProvider.getAudioPlayerViewModel()
            HomeView(navController, viewModel, audioPlayerViewModel)
        }

        composable(
            route = Destinations.Playlists
        ) {
            val viewModel: PlaylistsViewModel = viewModel()
            PlaylistsView(NavigationArgs.trackTitle, navController, viewModel)
        }
    }
}