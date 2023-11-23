package com.coolnexttech.fireplayer.model

data class Track(
    var title: String,
    var artist: String,
    var album: String,
    var path: String,
    var pathExtension: String,
    var playlist: String? = null
)
