package com.coolnexttech.fireplayer.utils.extensions

import com.coolnexttech.fireplayer.db.TrackEntity
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.SortOptions

fun List<TrackEntity>.sort(sortOption: SortOptions): List<TrackEntity> {
    return when(sortOption) {
        SortOptions.AToZ -> sortedBy { it.title }
        SortOptions.ZToA -> sortedByDescending { it.title }
        SortOptions.OldToNew -> sortedBy { it.dateAdded }
        SortOptions.NewToOld -> sortedByDescending { it.dateAdded }
    }
}

fun List<TrackEntity>.filter(filterOption: FilterOptions, text: String): List<TrackEntity> {
    val normalizedText = text.normalize()
    return when(filterOption) {
        FilterOptions.Title -> filter { it.title.normalize().contains(normalizedText, ignoreCase = true) }
        FilterOptions.Artist -> filter { it.artist.normalize().contains(normalizedText, ignoreCase = true) }
        FilterOptions.Album -> filter { it.album.normalize().contains(normalizedText, ignoreCase = true) }
    }
}

fun List<TrackEntity>.nextTrack(playMode: PlayMode, prevTracks: List<TrackEntity>, selectedTrack: TrackEntity): TrackEntity? {
    return when (playMode) {
        PlayMode.Shuffle -> this.getNextRandomTrack(excludedTracks = prevTracks)
        PlayMode.Sequential -> this.getNextTrackAndIndex(selectedTrack)?.first
        PlayMode.Loop -> selectedTrack
    }
}

private fun List<TrackEntity>.getNextTrackAndIndex(track: TrackEntity?): Pair<TrackEntity, Int>? {
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

private fun List<TrackEntity>.getNextRandomTrack(excludedTracks: List<TrackEntity>): TrackEntity {
    val availableTracks = this.filterNot { it in excludedTracks }

    return if (availableTracks.isEmpty()) {
        this.random()
    } else {
        availableTracks.random()
    }
}

fun List<TrackEntity>.filterByPlaylist(titles: ArrayList<String>): List<TrackEntity> {
    return this.filter { track -> track.titleRepresentation() in titles }
}
