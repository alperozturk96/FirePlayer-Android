package com.coolnexttech.fireplayer.extensions

import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track

fun List<Track>.sort(sortOption: SortOptions): List<Track> {
    return when(sortOption) {
        SortOptions.AtoZ -> sortedBy { it.title }
        SortOptions.ZtoA -> sortedByDescending { it.title }
    }
}

fun List<Track>.filter(filterOption: FilterOptions, text: String): List<Track> {
    return when(filterOption) {
        FilterOptions.Title -> filter { it.title.contains(text, ignoreCase = true) }
        FilterOptions.Artist -> filter { it.artist.contains(text, ignoreCase = true) }
        FilterOptions.Album -> filter { it.album.contains(text, ignoreCase = true) }
    }
}
