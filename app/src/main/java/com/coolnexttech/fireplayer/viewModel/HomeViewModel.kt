package com.coolnexttech.fireplayer.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.extensions.filter
import com.coolnexttech.fireplayer.extensions.isTrackAvailable
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

    private var _tracks: List<Track> = arrayListOf()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _filterOption = MutableStateFlow(FilterOptions.Title)
    val filterOption: StateFlow<FilterOptions> = _filterOption

    private val _playMode = MutableStateFlow(PlayMode.Shuffle)
    val playMode: StateFlow<PlayMode> = _playMode

    private val _selectedTrackIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    val selectedTrackIndex: StateFlow<Int?> = _selectedTrackIndex

    private val _prevTrackIndexesStack = MutableStateFlow<ArrayList<Int>>(arrayListOf())

    private val _filteredTracks = MutableStateFlow<List<Track>>(arrayListOf())
    val filteredTracks: StateFlow<List<Track>> = _filteredTracks

    fun initTrackList(selectedPlaylistTitle: String?) {
        _tracks = if (selectedPlaylistTitle == null) {
            FolderAnalyzer.tracks
        } else {
            FolderAnalyzer.getTracksFromPlaylist(selectedPlaylistTitle)
        }

        _filteredTracks.update {
            _tracks
        }

        Log.d("Home", "Total Track Count: " + _tracks.count())
    }

    fun sort(sortOption: SortOptions) {
        _filteredTracks.update {
            it.sort(sortOption)
        }
    }

    fun changeFilterOption(value: String) {
        _filterOption.update {
            it.selectNextFilterOption()
        }

        search(value)
    }

    fun changePlayMode() {
        _playMode.update {
            it.selectNextPlayMode()
        }
    }

    fun updatePrevTracks() {
        _prevTrackIndexesStack.update {
            _selectedTrackIndex.value?.let { index -> it.add(index) }
            it
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
        if (index == _selectedTrackIndex.value) {
            val audioPlayerViewModel = ViewModelProvider.audioPlayerViewModel()
            val track = _tracks[index]
            audioPlayerViewModel.play(track.path)
        } else {
            _selectedTrackIndex.update {
                index
            }
        }
    }

    fun selectNextTrack() {
        if (_filteredTracks.value.isEmpty()) {
            return
        }

        if (_selectedTrackIndex.value == null) {
            return
        }

        val nextIndex = when (_playMode.value) {
            PlayMode.Shuffle -> _filteredTracks.value.indices.random()
            PlayMode.Sequential -> (_selectedTrackIndex.value!! + 1).takeIf { it < _filteredTracks.value.size } ?: 0
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
                _tracks.sort(SortOptions.AtoZ)
            } else {
                _tracks.filter(_filterOption.value, value).sort(SortOptions.AtoZ)
            }
        }
    }

    fun currentTrackTitle(): String {
        val index = _selectedTrackIndex.value ?: return ""
        return if (_filteredTracks.value.isTrackAvailable()) {
            _filteredTracks.value[index].title
        } else {
            ""
        }
    }
}
