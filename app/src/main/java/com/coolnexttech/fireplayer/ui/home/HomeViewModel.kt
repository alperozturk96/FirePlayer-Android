package com.coolnexttech.fireplayer.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.ViewModelProvider
import com.coolnexttech.fireplayer.utils.extensions.filter
import com.coolnexttech.fireplayer.utils.extensions.getNextTrack
import com.coolnexttech.fireplayer.utils.extensions.sort
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel : ViewModel() {

    private var _tracks: List<Track> = arrayListOf()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _filterOption = MutableStateFlow(FilterOptions.Title)
    val filterOption: StateFlow<FilterOptions> = _filterOption

    private val _playMode = MutableStateFlow(PlayMode.Shuffle)
    val playMode: StateFlow<PlayMode> = _playMode

    private val _selectedTrack: MutableStateFlow<Track?> = MutableStateFlow(null)
    val selectedTrack: StateFlow<Track?> = _selectedTrack

    private val _prevTracks = MutableStateFlow<ArrayList<Track>>(arrayListOf())

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
        _prevTracks.update {
            _selectedTrack.value?.let { index -> it.add(index) }
            it
        }
    }

    fun selectPreviousTrack() {
        if (_prevTracks.value.size > 1) {
            _prevTracks.value.removeLast()
            _prevTracks.value.lastOrNull()?.let { prevIndex ->
                _selectedTrack.update {
                    prevIndex
                }
            }
        }
    }

    fun selectTrack(track: Track) {
        if (track.id == _selectedTrack.value?.id) {
            val audioPlayerViewModel = ViewModelProvider.audioPlayerViewModel()
            audioPlayerViewModel.play(track.path)
        } else {
            _selectedTrack.update {
                track
            }
        }
    }

    fun selectNextTrack() {
        if (_filteredTracks.value.isEmpty() || _selectedTrack.value == null) {
            return
        }

        if (_filteredTracks.value.size == 1) {
            selectTrack(_filteredTracks.value.first())
        }

        val nextTrack = getNextTrack() ?: return

        if (_prevTracks.value.last() == nextTrack) {
            selectTrack(nextTrack)
        } else {
            _prevTracks.update {
                it.add(nextTrack)
                it
            }

            _selectedTrack.update {
                nextTrack
            }
        }
    }

    private fun getNextTrack(): Track? {
        return when (_playMode.value) {
            PlayMode.Shuffle -> _filteredTracks.value.random()
            PlayMode.Sequential -> _filteredTracks.value.getNextTrack(_selectedTrack.value)?.first
        }
    }

    fun search(value: String) {
        _searchText.update {
            value
        }

        _filteredTracks.update {
            if (value.isEmpty()) {
                _tracks.sort(SortOptions.AToZ)
            } else {
                _tracks.filter(_filterOption.value, value).sort(SortOptions.AToZ)
            }
        }
    }

    fun currentTrackTitle(): String {
        return _selectedTrack.value?.title ?: return ""
    }
}
