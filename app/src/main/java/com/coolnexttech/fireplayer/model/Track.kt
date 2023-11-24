package com.coolnexttech.fireplayer.model

import android.net.Uri

data class Track(
    var id: Long,
    var title: String,
    var artist: String,
    var album: String,
    var path: Uri,
    var duration: Long,
    var pathExtension: String? = null,
    var playlist: String? = null
)
