package com.coolnexttech.fireplayer.ui.navigation

import androidx.compose.runtime.Composable
import com.coolnexttech.fireplayer.ui.home.HomeScreen
import com.coolnexttech.fireplayer.ui.info.InfoScreen
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsScreen
import com.coolnexttech.fireplayer.utils.VMProvider
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavHost
import dev.olshevski.navigation.reimagined.rememberNavController

@Composable
fun NavHostScreen() {
    val navController = rememberNavController<Destination>(
        startDestination = Destination.Home
    )

    NavBackHandler(navController)

    NavHost(navController) { destination ->
        when (destination) {
            is Destination.Home -> {
                HomeScreen(navController, VMProvider.homeViewModel, VMProvider.audioPlayer)
            }

            is Destination.Info -> {
                InfoScreen()
            }

            is Destination.Playlists -> {
                PlaylistsScreen(
                    navController,
                    mode = destination.playlistViewMode,
                    viewModel = VMProvider.playlistViewModel
                )
            }
        }
    }
}
