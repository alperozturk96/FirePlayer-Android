package com.coolnexttech.fireplayer.ui.playlists

import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.db.PlaylistBox
import com.coolnexttech.fireplayer.db.PlaylistEntity
import com.coolnexttech.fireplayer.db.TrackEntity
import com.coolnexttech.fireplayer.utils.UserStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class PlaylistsViewModel: ViewModel() {

    private val _playlists = MutableStateFlow<List<PlaylistEntity>>(listOf())
    val playlists: StateFlow<List<PlaylistEntity>> = _playlists

    init {
        readPlaylists()
    }

    fun readPlaylists() {
        _playlists.update {
            UserStorage.readPlaylists()
        }
    }

    fun addPlaylist(title: String) {
        val playlistEntity = PlaylistEntity(title = title)
        UserStorage.savePlaylists(playlistEntity)


        _playlists.update {
            UserStorage.readPlaylists()
        }
    }

    fun addTrackToPlaylist(track: TrackEntity, playlistTitle: String) {
        val playlistEntity = PlaylistBox.getByTitle(playlistTitle) ?: return
        playlistEntity.tracks.add(track)
        UserStorage.savePlaylists(playlistEntity)

        _playlists.update {
            UserStorage.readPlaylists()
        }
    }

    fun removePlaylist(title: String) {
        PlaylistBox.removeByTitle(title)

        _playlists.update {
            UserStorage.readPlaylists()
        }
    }
}
