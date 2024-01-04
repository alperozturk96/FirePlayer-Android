package com.coolnexttech.fireplayer.model

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
data class Track(
    var id: Long,
    var title: String,
    var artist: String,
    var album: String,
    var path: Uri,
    var duration: Long,
    var pathExtension: String? = null,
    var dateAdded: Long,
) {
    fun titleRepresentation(): String {
        return title + " · " + trackDetail()
    }

    fun trackDetail(): String {
        return "$artist · $album"
    }
}
