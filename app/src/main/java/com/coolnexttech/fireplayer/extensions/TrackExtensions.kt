package com.coolnexttech.fireplayer.extensions

import com.coolnexttech.fireplayer.model.Track

fun List<Track>.sortByTitleAZ(): List<Track> {
    return this.sortedBy { it.title }
}

fun List<Track>.sortByTitleZA(): List<Track> {
    return this.sortedByDescending { it.title }
}

fun List<Track>.filterByTitle(title: String): List<Track> {
    return this.filter { it.title.contains(title, ignoreCase = true) }
}

fun List<Track>.filterByArtist(artist: String): List<Track> {
    return this.filter { it.artist.contains(artist, ignoreCase = true) }
}

fun List<Track>.filterByAlbum(album: String): List<Track> {
    return this.filter { it.album.contains(album, ignoreCase = true) }
}

val List<Track>.randomIndex: Int
    get() = (indices).random()