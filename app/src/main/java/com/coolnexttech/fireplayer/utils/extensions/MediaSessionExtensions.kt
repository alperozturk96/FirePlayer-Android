package com.coolnexttech.fireplayer.utils.extensions

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaSession
import com.coolnexttech.fireplayer.model.Track

fun MediaSession?.play(track: Track) {
    val mediaItem = MediaItem.Builder()
        .setMediaId(track.id.toString())
        .setUri(track.path)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setTitle(track.title)
                .setArtist(track.artist)
                .build()
        )
        .build()

    this?.player?.apply {
        stop()
        clearMediaItems()
        setMediaItem(mediaItem)
        prepare()
        play()
    }
}