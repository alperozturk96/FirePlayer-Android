package com.coolnexttech.fireplayer.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.db.PlaylistBox
import com.coolnexttech.fireplayer.db.PlaylistEntity
import com.coolnexttech.fireplayer.db.TrackPlaybackBox
import com.coolnexttech.fireplayer.utils.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.utils.extensions.toJson

object UserStorage {

    private const val APP = "FirePlayer"

    private val sharedPreferences by lazy {
        appContext.get()?.getSharedPreferences(APP, MODE_PRIVATE)
    }

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
        val result = sharedPreferences?.getLong(id.toString(), 0L)
        return if (result == 0L) {
            null
        } else {
            if (showToast && result != null) {
                ToastManager.showReadTrackPlaybackPositionMessage(result)
            }

            result
        }
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

        val playlist = playlistsAsJson.jsonToPlaylists()
        savePlaylists(playlist)

        ToastManager.showSuccessImportPlaylistMessage()
    }
}
