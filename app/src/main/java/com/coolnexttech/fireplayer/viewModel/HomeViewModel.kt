package com.coolnexttech.fireplayer.viewModel

import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.extensions.filterByAlbum
import com.coolnexttech.fireplayer.extensions.filterByArtist
import com.coolnexttech.fireplayer.extensions.filterByTitle
import com.coolnexttech.fireplayer.extensions.sortByTitleAZ
import com.coolnexttech.fireplayer.extensions.sortByTitleZA
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel: ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(arrayListOf())

    private val _filteredTracks = MutableStateFlow<List<Track>>(arrayListOf())
    val filteredTracks: StateFlow<List<Track>> = _filteredTracks

    fun initTrackList(folderAnalyzer: FolderAnalyzer) {
        _tracks.update {
            folderAnalyzer.getTracksFromMusicFolder()
        }

        _filteredTracks.update {
            _tracks.value
        }
    }

    fun updateTrackList(tracks: List<Track>) {
        _filteredTracks.update {
            tracks
        }
    }

    fun sort(isAtoZ: Boolean) {
        _filteredTracks.update {
            if (isAtoZ) {
                it.sortByTitleAZ()
            } else {
                it.sortByTitleZA()
            }
        }
    }

    fun search(searchText: String, filterOption: FilterOptions) {
        _filteredTracks.update {
            if (searchText.isEmpty()) {
                _tracks.value.sortByTitleAZ()
            } else {
                when(filterOption) {
                    FilterOptions.title -> it.filterByTitle(searchText).sortByTitleAZ()
                    FilterOptions.artist -> it.filterByArtist(searchText).sortByTitleAZ()
                    FilterOptions.album -> it.filterByAlbum(searchText).sortByTitleAZ()
                }
            }
        }
    }

}