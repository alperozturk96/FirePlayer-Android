package com.coolnexttech.fireplayer.viewModel

import com.coolnexttech.fireplayer.app.FirePlayer

object ViewModelProvider {
    val homeViewModel: HomeViewModel by lazy { HomeViewModel() }
    val playlistViewModel: PlaylistsViewModel by lazy { PlaylistsViewModel() }
    val audioPlayerViewModel: AudioPlayerViewModel by lazy {
        AudioPlayerViewModel(FirePlayer.context)
    }
}