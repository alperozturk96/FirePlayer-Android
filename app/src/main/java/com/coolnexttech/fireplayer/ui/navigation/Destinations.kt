package com.coolnexttech.fireplayer.ui.navigation

import androidx.navigation.NavHostController
import com.coolnexttech.fireplayer.model.PlaylistViewMode

object Destinations {
    const val Home = "Home"
    const val Playlists = "Playlists"

    fun navigateToHomeWithPlaylist(selectedPlaylist: String, navController: NavHostController) {
        NavigationArgs.selectedPlaylistTitle = selectedPlaylist
        navController.navigate(Home) {
            popUpTo(Home) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToPlaylists(
        playlistViewMode: PlaylistViewMode,
        trackTitle: String,
        navController: NavHostController
    ) {
        NavigationArgs.playlistViewMode = playlistViewMode
        NavigationArgs.trackTitle = trackTitle

        navController.navigate(Playlists)
    }
}
