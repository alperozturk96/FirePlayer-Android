package com.coolnexttech.fireplayer.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class PlaylistViewMode: Parcelable {
    @Parcelize
    data class Add(val trackId: Long) : PlaylistViewMode()

    @Parcelize
    data object Select : PlaylistViewMode()
}