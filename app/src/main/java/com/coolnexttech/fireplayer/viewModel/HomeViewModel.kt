package com.coolnexttech.fireplayer.viewModel

import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.extensions.filter
import com.coolnexttech.fireplayer.extensions.sort
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.SortOptions
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

    fun sort(sortOption: SortOptions) {
        _filteredTracks.update {
            it.sort(sortOption)
        }
    }

    fun search(searchText: String, filterOption: FilterOptions) {
        _filteredTracks.update {
            if (searchText.isEmpty()) {
                _tracks.value.sort(SortOptions.AtoZ)
            } else {
                it.filter(filterOption, searchText).sort(SortOptions.AtoZ)
            }
        }
    }

}