package com.coolnexttech.fireplayer.ui.navigation

import android.os.Parcelable
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import kotlinx.parcelize.Parcelize

sealed class Destination : Parcelable {
    @Parcelize
    data class Home(val selectedPlaylistTitle: String?) : Destination()

    @Parcelize
    data class Playlists(val playlistViewMode: PlaylistViewMode) : Destination()
}