package com.coolnexttech.fireplayer.utils.extensions

import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
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

fun List<Track>.nextTrack(playMode: PlayMode, prevTracks: List<Track>, selectedTrack: Track): Track? {
    return when (playMode) {
        PlayMode.Shuffle -> this.getNextRandomTrack(excludedTracks = prevTracks)
        PlayMode.Sequential -> this.getNextTrackAndIndex(selectedTrack)?.first
        PlayMode.Loop -> selectedTrack
    }
}

private fun List<Track>.getNextTrackAndIndex(track: Track?): Pair<Track, Int>? {
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

private fun List<Track>.getNextRandomTrack(excludedTracks: List<Track>): Track {
    val availableTracks = this.filterNot { it in excludedTracks }

    return if (availableTracks.isEmpty()) {
        this.random()
    } else {
        availableTracks.random()
    }
}

fun List<Track>.filterByPlaylist(titles: ArrayList<String>): List<Track> {
    return this.filter { track -> track.titleRepresentation() in titles }
}
