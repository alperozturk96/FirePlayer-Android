package com.coolnexttech.fireplayer.ui.navigation

import com.coolnexttech.fireplayer.model.PlaylistViewMode

object NavigationArgs {
    var selectedPlaylistTitle: String? = null
    lateinit var playlistViewMode: PlaylistViewMode
    lateinit var trackTitle: String
}