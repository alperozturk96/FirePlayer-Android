package com.coolnexttech.fireplayer.model

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.coolnexttech.fireplayer.ui.theme.AppColors

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
    val isPositionSaved: Boolean
) {
    fun titleRepresentation(): String {
        return title + " · " + trackDetail()
    }

    fun color(selectedTrackId: Long?): Color {
        if (isPositionSaved) {
            return AppColors.saved
        }

        if (selectedTrackId == id) {
            return AppColors.highlight
        }

        return AppColors.textColor
    }

    private fun trackDetail(): String {
        return "$artist · $album"
    }
}
