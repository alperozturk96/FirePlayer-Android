package com.coolnexttech.fireplayer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.coolnexttech.fireplayer.util.FolderAnalyzer
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
            val selectedPlaylistTitle = NavigationArgs.selectedPlaylistTitle
            val context = LocalContext.current
            val folderAnalyzer = FolderAnalyzer(context)

            val viewModel: HomeViewModel = ViewModelProvider.getHomeViewModel()
            viewModel.initTrackList(folderAnalyzer, selectedPlaylistTitle)
            val audioPlayerViewModel: AudioPlayerViewModel =
                ViewModelProvider.getAudioPlayerViewModel()
            HomeView(navController, viewModel, audioPlayerViewModel)
        }

        composable(Destinations.Playlists) {
            val viewModel: PlaylistsViewModel = viewModel()
            PlaylistsView(NavigationArgs.trackTitle, navController, viewModel)
        }
    }
}