package com.coolnexttech.fireplayer.ui.navigation

import androidx.navigation.NavHostController

object Destinations {
    const val Home = "Home"
    const val Playlists = "Playlists"

    fun navigateToPlaylists(
        isAdd: Boolean,
        navController: NavHostController
    ) {
        navController.navigate("$Playlists/${isAdd}")
    }
}