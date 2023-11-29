package com.coolnexttech.fireplayer.extensions

import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.viewModel.ViewModelProvider

fun List<Track>.sort(sortOption: SortOptions): List<Track> {
    return when(sortOption) {
        SortOptions.AtoZ -> sortedBy { it.title }
        SortOptions.ZtoA -> sortedByDescending { it.title }
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

fun List<Track>.filterByPlaylist(titles: ArrayList<String>): List<Track> {
    return this.filter { track -> track.title in titles }
}

fun List<Track>.isTrackAvailable(): Boolean {
    val homeViewModel = ViewModelProvider.homeViewModel
    val selectedTrackIndex = homeViewModel.selectedTrackIndex.value ?: return false
    return selectedTrackIndex >= 0 && selectedTrackIndex < this.size
}
