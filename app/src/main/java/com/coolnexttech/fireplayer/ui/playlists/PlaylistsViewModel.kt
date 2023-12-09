package com.coolnexttech.fireplayer.ui.playlists

import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.model.Playlists
import com.coolnexttech.fireplayer.utils.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PlaylistsViewModel: ViewModel() {

    private val _playlists = MutableStateFlow<Playlists>(hashMapOf())
    val playlists: StateFlow<Playlists> = _playlists

    private var storage: UserStorage? = null

    init {
        readPlaylists()
    }

    private fun readPlaylists() {
        _playlists.update {
            UserStorage.readPlaylists()
        }
    }

    fun addPlaylist(title: String) {
        _playlists.update {
            it[title] = arrayListOf()
            storage?.savePlaylists(it)
            it
        }
    }

    fun addTrackToPlaylist(trackId: Long, playlistTitle: String) {
        _playlists.update {
            it[playlistTitle]?.add(trackId)
            storage?.savePlaylists(it)
            it
        }
    }

    // FIXME
    fun removePlaylist(title: String) {
        _playlists.update {
            it.remove(title)
            storage?.savePlaylists(it)
            it
        }
    }
}