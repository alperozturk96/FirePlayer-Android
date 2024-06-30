package com.coolnexttech.fireplayer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coolnexttech.fireplayer.db.TrackEntity
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.filter
import com.coolnexttech.fireplayer.utils.extensions.nextTrack
import com.coolnexttech.fireplayer.utils.extensions.sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _filterOption = MutableStateFlow(FilterOptions.Title)
    val filterOption: StateFlow<FilterOptions> = _filterOption

    private val _playMode = MutableStateFlow(PlayMode.Shuffle)
    val playMode: StateFlow<PlayMode> = _playMode

    private val _isPlaylistSelected = MutableStateFlow(false)
    val isPlaylistSelected: StateFlow<Boolean> = _isPlaylistSelected

    private val _selectedTrack: MutableStateFlow<TrackEntity?> = MutableStateFlow(null)
    val selectedTrack: StateFlow<TrackEntity?> = _selectedTrack

    private var _tracks: List<TrackEntity> = listOf()
    private val _prevTracks = MutableStateFlow<ArrayList<TrackEntity>>(arrayListOf())

    private val _filteredTracks = MutableStateFlow<List<TrackEntity>>(arrayListOf())
    val filteredTracks: StateFlow<List<TrackEntity>> = _filteredTracks

    private val _showLoadingDialog = MutableStateFlow(false)
    val showLoadingDialog: StateFlow<Boolean> = _showLoadingDialog

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            _showLoadingDialog.update {
                true
            }

            _tracks = FolderAnalyzer.getTracks(null)
            initTrackList(null)

            _showLoadingDialog.update {
                false
            }
        }
    }

    fun initTrackList(tracksInPlaylist: List<TrackEntity>?) {
        viewModelScope.launch(Dispatchers.IO) {
            val newTracks = tracksInPlaylist ?: _tracks

            _isPlaylistSelected.update {
                tracksInPlaylist != null
            }

            _filteredTracks.update {
                newTracks
            }
        }
    }

    fun sort(sortOption: SortOptions) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = withContext(Dispatchers.IO) {
                _filteredTracks.value.sort(sortOption)
            }

            _filteredTracks.update {
                result
            }
        }
    }

    fun deleteTrack(track: TrackEntity) {
        _tracks = _tracks.filter { it != track }
        _filteredTracks.update {
            _tracks
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

    fun playTrack(track: TrackEntity, updatePrevStack: Boolean = true) {
        VMProvider.audioPlayer.play(track, onSuccess = {
            updateSelectedTrack(track)

            if (updatePrevStack) {
                updatePrevTracks(track)
            }
        })
    }

    private fun updateSelectedTrack(track: TrackEntity) {
        _selectedTrack.update {
            track
        }
    }

    private fun updatePrevTracks(track: TrackEntity) {
        _prevTracks.update {
            if (it.size == _filteredTracks.value.size) {
                it.clear()
                it
            } else {
                if (!it.contains(track)) {
                    it.add(track)
                }

                it
            }
        }
    }

    fun handlePlayerEvent(action: String?) {
        when (action) {
            PlayerEvents.Previous.name -> {
                playPreviousTrack()
            }

            PlayerEvents.Next.name -> {
                playNextTrack()
            }
        }
    }

    fun playNextTrack() {
        viewModelScope.launch {
            if (_filteredTracks.value.isEmpty() || _selectedTrack.value == null) {
                return@launch
            }

            if (_filteredTracks.value.size == 1) {
                playTrack(_filteredTracks.value.first())
            }

            val nextTrack = withContext(Dispatchers.IO) {
                _filteredTracks.value.nextTrack(
                    _playMode.value,
                    _prevTracks.value,
                    _selectedTrack.value!!
                )
            } ?:  return@launch

            playTrack(nextTrack)
        }
    }

    fun update() {
        viewModelScope.launch(Dispatchers.IO) {
            _showLoadingDialog.update {
                true
            }
            _tracks = FolderAnalyzer.addScannedTracksToTracksEntity(null)
            clearSearch()
            initTrackList(null)
            hideLoadingDialog()
        }
    }

    fun hideLoadingDialog() {
        _showLoadingDialog.update {
            false
        }
    }

    fun clearSearch() {
        search("")
    }

    fun search(value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchText.update {
                value
            }

            val result = withContext(Dispatchers.IO) {
                if (value.isEmpty()) {
                    _tracks.sort(SortOptions.AToZ)
                } else {
                    _tracks.filter(_filterOption.value, value).sort(SortOptions.AToZ)
                }
            }

            _filteredTracks.update {
                result
            }
        }
    }
}
