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
    private val maxTitleCharSize = 30

    fun seekBarTitleRepresentation(): String {
        return titleRepresentation() + " · " + trackDetail()
    }

    fun trackDetail(): String {
        return "$artist · $album"
    }

    fun titleRepresentation(): String {
        return if (title.length > maxTitleCharSize) {
            title.take(maxTitleCharSize) + "..."
        } else {
            title
        }
    }
}
