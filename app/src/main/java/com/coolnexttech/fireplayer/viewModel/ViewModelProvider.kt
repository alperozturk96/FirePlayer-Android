package com.coolnexttech.fireplayer.viewModel

import com.coolnexttech.fireplayer.app.FirePlayer

object ViewModelProvider {
    private var homeViewModel: HomeViewModel? = null
    private var playlistViewModel: PlaylistsViewModel? = null
    private var audioPlayerViewModel: AudioPlayerViewModel? = null

    fun getHomeViewModel(): HomeViewModel {
        if (homeViewModel == null) {
            homeViewModel = HomeViewModel()
        }

        return homeViewModel!!
    }

    fun getPlaylistViewModel(): PlaylistsViewModel {
        if (playlistViewModel == null) {
            playlistViewModel = PlaylistsViewModel()
        }

        return playlistViewModel!!
    }

    fun getAudioPlayerViewModel(): AudioPlayerViewModel {
        if (audioPlayerViewModel == null) {
            audioPlayerViewModel = AudioPlayerViewModel(FirePlayer.context)
        }

        return audioPlayerViewModel!!
    }


}