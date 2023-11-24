package com.coolnexttech.fireplayer.viewModel

import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel: ViewModel() {

    private val _trackList = MutableStateFlow<List<Track>>(listOf())
    val trackList: StateFlow<List<Track>> = _trackList

    fun initTrackList(folderAnalyzer: FolderAnalyzer) {
        _trackList.update {
            folderAnalyzer.getTracks()
        }
    }

}