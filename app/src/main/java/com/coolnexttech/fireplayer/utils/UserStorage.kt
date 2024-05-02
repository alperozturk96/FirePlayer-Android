package com.coolnexttech.fireplayer.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.model.Playlists
import com.coolnexttech.fireplayer.utils.extensions.jsonToPlaylists
import com.coolnexttech.fireplayer.utils.extensions.toJson

object UserStorage {

    private const val APP = "FirePlayer"
    private const val PLAYLISTS = "playlists"

    private val sharedPreferences by lazy {
        appContext.get()?.getSharedPreferences(APP, MODE_PRIVATE)
    }

    fun readPlaylists(): Playlists {
        return sharedPreferences?.getString(PLAYLISTS, null)?.jsonToPlaylists() ?: hashMapOf()
    }

    fun savePlaylists(value: Playlists) {
        sharedPreferences?.edit()?.putString(PLAYLISTS, value.toJson())?.apply()
    }

    fun removeTrackPlaybackPosition(id: Long) {
        sharedPreferences?.edit()?.remove(id.toString())?.apply()
        ToastManager.showRemoveTrackPlaybackPosition()
    }

    fun saveTrackPlaybackPosition(id: Long?, position: Long?) {
        if (id == null || position == null) return
        sharedPreferences?.edit()?.putLong(id.toString(), position)?.apply()
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
