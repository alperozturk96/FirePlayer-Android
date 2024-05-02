package com.coolnexttech.fireplayer.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coolnexttech.fireplayer.ui.home.HomeScreen
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsScreen
import com.coolnexttech.fireplayer.utils.VMProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen() {
    var screen by remember { mutableStateOf(Screen.Home) }

    when (screen) {
        Screen.Home -> {
            HomeScreen(
                VMProvider.homeViewModel,
                VMProvider.playlistViewModel,
                VMProvider.audioPlayer,
                navigateToPlaylists = {
                    screen = Screen.Playlist
                }
            )
        }

        Screen.Playlist -> {
            PlaylistsScreen(
                homeViewModel = VMProvider.homeViewModel,
                viewModel = VMProvider.playlistViewModel
            ) {
                screen = Screen.Home
            }
        }
    }
}
