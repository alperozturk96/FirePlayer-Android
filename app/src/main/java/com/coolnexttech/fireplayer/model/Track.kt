package com.coolnexttech.fireplayer.model

data class Track(
    var id: Long,
    var title: String,
    var artist: String,
    var album: String,
    var path: String,
    var pathExtension: String? = null,
    var playlist: String? = null
)
