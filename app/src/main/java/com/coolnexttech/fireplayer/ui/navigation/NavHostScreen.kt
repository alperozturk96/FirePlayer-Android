package com.coolnexttech.fireplayer.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import com.coolnexttech.fireplayer.view.HomeView
import com.coolnexttech.fireplayer.view.PlaylistsView
import com.coolnexttech.fireplayer.viewModel.ViewModelProvider
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun NavHostScreen(context: Context) {
    val navController = rememberNavController<Destination>(
        startDestination = Destination.Home(null)
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            is Destination.Home -> {
                val homeViewModel = ViewModelProvider.homeViewModel
                val audioPlayerViewModel = ViewModelProvider.audioPlayerViewModel
                val folderAnalyzer = FolderAnalyzer(context)
                homeViewModel.initTrackList(folderAnalyzer, destination.selectedPlaylistTitle)
                HomeView(navController, homeViewModel, audioPlayerViewModel)
            }

            is Destination.Playlists -> {
                PlaylistsView(
                    navController,
                    trackTitle = destination.trackTitle,
                    viewMode = destination.playlistViewMode,
                    viewModel = ViewModelProvider.playlistViewModel
                )
            }
        }
    }
}