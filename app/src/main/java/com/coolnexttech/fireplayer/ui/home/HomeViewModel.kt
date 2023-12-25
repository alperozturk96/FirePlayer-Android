package com.coolnexttech.fireplayer.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.FirePlayer
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.filter
import com.coolnexttech.fireplayer.utils.extensions.getNextRandomTrack
import com.coolnexttech.fireplayer.utils.extensions.getNextTrackAndIndex
import com.coolnexttech.fireplayer.utils.extensions.sort
import com.coolnexttech.fireplayer.utils.extensions.startPlayerService
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

    init {
        initTrackList(null)
    }

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

    fun playPreviousTrack() {
        if (_prevTracks.value.size > 1) {
            _prevTracks.value.removeLast()
            _prevTracks.value.lastOrNull()?.let { prevTrack ->
                playTrack(prevTrack, false)
            }
        }
    }

    fun playTrack(track: Track, updatePrevStack: Boolean = true) {
        updateSelectedTrack(track)
        VMProvider.audioPlayer.play(track.path)

        if (updatePrevStack) {
            updatePrevTracks(track)
        }

        FirePlayer.context.startPlayerService()
    }

    private fun updateSelectedTrack(track: Track) {
        _selectedTrack.update {
            track
        }
    }

    private fun updatePrevTracks(track: Track) {
        _prevTracks.update {
            if (it.size == _filteredTracks.value.size) {
                it.clear()
                it
            } else {
                it.add(track)
                it
            }
        }
    }

    fun playNextTrack() {
        if (_filteredTracks.value.isEmpty() || _selectedTrack.value == null) {
            return
        }

        if (_filteredTracks.value.size == 1) {
            playTrack(_filteredTracks.value.first())
        }

        val nextTrack = when (_playMode.value) {
            PlayMode.Shuffle -> _filteredTracks.value.getNextRandomTrack(excludedTracks = _prevTracks.value)
            PlayMode.Sequential -> _filteredTracks.value.getNextTrackAndIndex(_selectedTrack.value)?.first
        } ?: return

        playTrack(nextTrack)
    }

    fun reset() {
        clearSearch()
        _filterOption.update {
            FilterOptions.Title
        }
        _playMode.update {
            PlayMode.Shuffle
        }

        FolderAnalyzer.initTracksFromMusicFolder()
        initTrackList(null)
    }

    fun clearSearch() {
        _searchText.update {
            ""
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

    fun currentTrackDetail(): String {
        return _selectedTrack.value?.trackDetail() ?: return ""
    }
}
