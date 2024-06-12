package com.coolnexttech.fireplayer.db

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.coolnexttech.fireplayer.ui.theme.AppColors
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany

@Entity
data class TrackEntity(
    @Id var id: Long = 0,
    var title: String,
    var artist: String,
    var album: String,
    var path: String,
    var duration: Long,
    var pathExtension: String? = null,
    var dateAdded: Long,
    var savedPosition: Long? = null
) {
    lateinit var playlists: ToMany<PlaylistEntity>

    fun getUri(): Uri = Uri.parse(path)

    fun titleRepresentation(): String {
        return title + " · " + trackDetail()
    }

    fun color(selectedTrackId: Long?): Color {
        if (savedPosition != null) {
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