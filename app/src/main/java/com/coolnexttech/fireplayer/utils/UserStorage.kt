package com.coolnexttech.fireplayer.utils

import android.content.Context
import com.coolnexttech.fireplayer.db.PlaylistBox
import com.coolnexttech.fireplayer.db.PlaylistEntity
import com.coolnexttech.fireplayer.db.TrackPlaybackBox
import com.coolnexttech.fireplayer.utils.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.utils.extensions.toJson

object UserStorage {

    fun readPlaylists(): List<PlaylistEntity> = PlaylistBox.getAll()

    fun savePlaylists(entity: PlaylistEntity) = PlaylistBox.add(entity)

    fun removeTrackPlaybackPosition(id: Long) {
        TrackPlaybackBox.remove(id)
        ToastManager.showRemoveTrackPlaybackPosition()
    }

    fun saveTrackPlaybackPosition(id: Long?, position: Long?) {
        if (id == null || position == null) return
        TrackPlaybackBox.add(id, position)
        ToastManager.showSaveTrackPlaybackPositionMessage()
    }

    fun readTrackPlaybackPosition(id: Long, showToast: Boolean): Long? {
        val result = TrackPlaybackBox.read(id)?.position ?: return null
        if (showToast) {
            ToastManager.showReadTrackPlaybackPositionMessage(result)
        }
        return result
    }

    fun exportPlaylists(context: Context) {
        val playlists = readPlaylists()
        val playlistsAsJson = playlists.toJson()
        val success = FileManager.writeToDocuments(context, "FirePlayerPlaylists", playlistsAsJson)
        if (success) {
            ToastManager.showSuccessExportPlaylistMessage()
        } else {
            ToastManager.showFailExportPlaylistMessage()
        }
    }

    fun importPlaylist(playlistsAsJson: String?) {
        if (playlistsAsJson == null) {
            ToastManager.showFailImportPlaylistMessage()
            return
        }

        val playlists = playlistsAsJson.jsonToPlaylists()
        PlaylistBox.addAll(playlists)

        ToastManager.showSuccessImportPlaylistMessage()
    }
}
