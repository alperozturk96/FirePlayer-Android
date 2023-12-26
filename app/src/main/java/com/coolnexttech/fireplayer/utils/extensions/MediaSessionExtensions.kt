package com.coolnexttech.fireplayer.utils.extensions

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession

fun MediaSession?.play(uri: Uri) {
    this?.player?.apply {
        stop()
        clearMediaItems()
        val mediaItem: MediaItem = MediaItem.fromUri(uri)
        setMediaItem(mediaItem)
        prepare()
        play()
    }
}