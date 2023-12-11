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
import com.coolnexttech.fireplayer.utils.extensions.getTrackById
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

    // TODO only use selected track
    private val _selectedTrackIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    val selectedTrackIndex: StateFlow<Int?> = _selectedTrackIndex

    private val _selectedTrackId: MutableStateFlow<Long?> = MutableStateFlow(null)
    val selectedTrackId: StateFlow<Long?> = _selectedTrackId

    private val _prevTrackIdsStack = MutableStateFlow<ArrayList<Long>>(arrayListOf())

    private val _filteredTracks = MutableStateFlow<List<Track>>(arrayListOf())
    val filteredTracks: StateFlow<List<Track>> = _filteredTracks

    private var prevTrackId: Long? = null

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
        _prevTrackIdsStack.update {
            _selectedTrackId.value?.let { index -> it.add(index) }
            it
        }
    }

    fun selectPreviousTrack() {
        if (_prevTrackIdsStack.value.size > 1) {
            _prevTrackIdsStack.value.removeLast()
            _prevTrackIdsStack.value.lastOrNull()?.let { prevIndex ->
                _selectedTrackId.update {
                    prevIndex
                }
            }
        }
    }

    fun selectTrack(trackId: Long) {
        if (trackId == _selectedTrackId.value) {
            val audioPlayerViewModel = ViewModelProvider.audioPlayerViewModel()
            val trackPair = _filteredTracks.value.getTrackById(trackId) ?: return
            _selectedTrackIndex.update {
                trackPair.second
            }
            audioPlayerViewModel.play(trackPair.first.path)
        } else {
            _selectedTrackId.update {
                trackId
            }
        }
    }

    fun selectNextTrack() {
        if (_filteredTracks.value.isEmpty()) {
            return
        }

        if (_selectedTrackId.value == null) {
            return
        }

        if (_filteredTracks.value.size == 1) {
            selectTrack(0)
        }

        val nextTrackId = getNextTrackId() ?: return

        if (prevTrackId == nextTrackId) {
            selectTrack(nextTrackId)
        } else {
            prevTrackId = nextTrackId

            _selectedTrackId.update {
                nextTrackId
            }
        }
    }

    private fun getNextTrackId(): Long? {
        return when (_playMode.value) {
            PlayMode.Shuffle -> _filteredTracks.value.random().id
            PlayMode.Sequential -> _filteredTracks.value.getNextTrack(_selectedTrackId.value)?.id
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
        return _filteredTracks.value.getTrackById(_selectedTrackId.value)?.first?.title ?: return ""
    }
}
