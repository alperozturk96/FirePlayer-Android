package com.coolnexttech.fireplayer.utils

import com.coolnexttech.fireplayer.FirePlayer
import com.coolnexttech.fireplayer.ui.home.AudioPlayerViewModel
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel

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
