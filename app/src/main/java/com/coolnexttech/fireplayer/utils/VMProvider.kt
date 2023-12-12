package com.coolnexttech.fireplayer.utils

import com.coolnexttech.fireplayer.FirePlayer
import com.coolnexttech.fireplayer.ui.home.AudioPlayer
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel

object VMProvider {
    val homeViewModel = HomeViewModel()
    val playlistViewModel = PlaylistsViewModel()
    val audioPlayer = AudioPlayer(FirePlayer.context)
}
