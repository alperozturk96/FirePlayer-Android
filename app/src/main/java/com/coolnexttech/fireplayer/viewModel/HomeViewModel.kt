package com.coolnexttech.fireplayer.viewModel

import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.extensions.filter
import com.coolnexttech.fireplayer.extensions.sort
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel: ViewModel() {

    private val _tracks = MutableStateFlow<List<Track>>(arrayListOf())

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _filterOption = MutableStateFlow(FilterOptions.Title)
    val filterOption: StateFlow<FilterOptions> = _filterOption

    private val _playMode = MutableStateFlow(PlayMode.Shuffle)
    val playMode: StateFlow<PlayMode> = _playMode

    private val _selectedTrackIndex = MutableStateFlow(-1)
    val selectedTrackIndex: StateFlow<Int> = _selectedTrackIndex

    private val _prevTrackIndexesStack = MutableStateFlow<ArrayList<Int>>(arrayListOf())

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

    fun sort(sortOption: SortOptions) {
        _filteredTracks.update {
            it.sort(sortOption)
        }
    }

    fun changeFilterOption() {
        _filterOption.update {
            it.selectNextFilterOption()
        }
    }

    fun changePlayMode() {
        _playMode.update {
            it.selectNextPlayMode()
        }
    }

    fun selectPreviousTrack() {
        if (_prevTrackIndexesStack.value.size > 1) {
            _prevTrackIndexesStack.value.removeLast()
            _prevTrackIndexesStack.value.lastOrNull()?.let { prevIndex ->
                _selectedTrackIndex.update {
                    prevIndex
                }
            }
        }
    }

    fun selectTrack(index: Int) {
        _selectedTrackIndex.update {
            index
        }
        _prevTrackIndexesStack.update {
            it.add(index)
            it
        }
    }

    fun selectNextTrack() {
        if (_filteredTracks.value.isEmpty()) {
            return
        }

        val nextIndex = when (_playMode.value) {
            PlayMode.Shuffle -> _filteredTracks.value.indices.random()
            PlayMode.Sequential -> (_selectedTrackIndex.value + 1).takeIf { it < _filteredTracks.value.size } ?: 0
        }

        _selectedTrackIndex.update {
            nextIndex
        }
    }

    fun search(value: String) {
        _searchText.update {
            value
        }

        _filteredTracks.update {
            if (value.isEmpty()) {
                _tracks.value.sort(SortOptions.AtoZ)
            } else {
                it.filter(_filterOption.value, value).sort(SortOptions.AtoZ)
            }
        }
    }

    fun currentTrackTitle(): String {
        return _filteredTracks.value[_selectedTrackIndex.value].title
    }

}