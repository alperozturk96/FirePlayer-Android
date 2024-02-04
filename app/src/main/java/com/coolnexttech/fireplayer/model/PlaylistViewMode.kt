package com.coolnexttech.fireplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PlaylistViewMode: Parcelable {
    @Parcelize
    data class Add(val trackTitleRepresentation: String, val trackTitle: String) : PlaylistViewMode()

    @Parcelize
    data object Select : PlaylistViewMode()
}