package com.coolnexttech.fireplayer.viewModel

import com.coolnexttech.fireplayer.app.FirePlayer

object ViewModelProvider {
    private var homeViewModel: HomeViewModel? = null
    private var playlistViewModel: PlaylistsViewModel? = null
    private var audioPlayerViewModel: AudioPlayerViewModel? = null

    fun homeViewModel(): HomeViewModel {
        if (homeViewModel == null) {
            homeViewModel = HomeViewModel()
        }

        return homeViewModel!!
    }

    fun playlistViewModel(): PlaylistsViewModel {
        if (playlistViewModel == null) {
            playlistViewModel = PlaylistsViewModel()
        }

        return playlistViewModel!!
    }

    fun audioPlayerViewModel(): AudioPlayerViewModel {
        if (audioPlayerViewModel == null) {
            audioPlayerViewModel = AudioPlayerViewModel(FirePlayer.context)
        }

        return audioPlayerViewModel!!
    }
}
