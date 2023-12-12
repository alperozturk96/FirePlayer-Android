package com.coolnexttech.fireplayer.utils.extensions

import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track

fun List<Track>.sort(sortOption: SortOptions): List<Track> {
    return when(sortOption) {
        SortOptions.AToZ -> sortedBy { it.title }
        SortOptions.ZToA -> sortedByDescending { it.title }
        SortOptions.OldToNew -> sortedBy { it.dateAdded }
        SortOptions.NewToOld -> sortedByDescending { it.dateAdded }
    }
}

fun List<Track>.filter(filterOption: FilterOptions, text: String): List<Track> {
    val normalizedText = text.normalize()
    return when(filterOption) {
        FilterOptions.Title -> filter { it.title.normalize().contains(normalizedText, ignoreCase = true) }
        FilterOptions.Artist -> filter { it.artist.normalize().contains(normalizedText, ignoreCase = true) }
        FilterOptions.Album -> filter { it.album.normalize().contains(normalizedText, ignoreCase = true) }
    }
}

fun List<Track>.getNextTrack(track: Track?): Pair<Track, Int>? {
    this.forEachIndexed { index, newTrack ->
        if (track?.id == newTrack.id) {
            return if (index + 1 in this.indices) {
                Pair(this[index + 1], index)
            } else {
                Pair(this[0], index)
            }
        }
    }

    return null
}

fun List<Track>.filterByPlaylist(titles: ArrayList<Long>): List<Track> {
    return this.filter { track -> track.id in titles }
}
