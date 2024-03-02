package com.coolnexttech.fireplayer.ui.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.home.HomeScreen
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsScreen
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.VMProvider

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavigationBar() {
    var screen by remember { mutableStateOf(Screen.Home) }
    val items = listOf(
        Triple(
            stringResource(id = R.string.bottom_navigation_bar_home),
            Icons.Filled.Home,
            Screen.Home
        ),
        Triple(
            stringResource(id = R.string.bottom_navigation_bar_playlist),
            Icons.Filled.Favorite,
            Screen.Playlist,
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar(contentColor = AppColors.red) {
                items.forEachIndexed { _, item ->
                    NavigationBarItem(
                        icon = { Icon(item.second, contentDescription = item.first) },
                        label = { Text(item.first) },
                        selected = screen == item.third,
                        onClick = { screen = item.third }
                    )
                }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when (screen) {
                Screen.Home -> {
                    HomeScreen(
                        VMProvider.homeViewModel,
                        VMProvider.playlistViewModel,
                        VMProvider.audioPlayer
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
    }
}
