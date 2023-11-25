package com.coolnexttech.fireplayer.viewModel

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.coolnexttech.fireplayer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioPlayerViewModel: ViewModel() {

    private var mediaPlayer: MediaPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentTime = MutableStateFlow(0.0)
    val currentTime: StateFlow<Double> = _currentTime

    private val _totalTime = MutableStateFlow(0.0)
    val totalTime: StateFlow<Double> = _totalTime

    private var periodicUpdateJob: Job? = null

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    fun play(context: Context, uri: Uri) {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(context, uri)
            prepare()
            start()

            val durationInMillis = duration
            _totalTime.update {
                durationInMillis / 1000.0
            }
            _isPlaying.update {
                true
            }
            startPeriodicUpdateJob()
        }
    }

    fun togglePlayPause() {
        mediaPlayer?.apply {
            if (isPlaying) pause() else start()
            _isPlaying.update { isPlaying }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun updateCurrentTime(value: Double) {
        _currentTime.update {
            value
        }
    }

    fun seekTo(time: Double) {
        mediaPlayer?.let {
            val timeInMillis = (time * 1000).toInt()
            it.seekTo(timeInMillis)
            updateCurrentTime(time)
        }
    }

    fun toggleIconTextId(): Int {
        return if (_isPlaying.value) {
            R.string.media_control_pause_text
        } else {
            R.string.media_control_play_text
        }
    }

    private fun startPeriodicUpdateJob() {
        periodicUpdateJob?.cancel()
        periodicUpdateJob = coroutineScope.launch {
            while (isActive) {
                val currentPosInMillis = mediaPlayer?.currentPosition
                _currentTime.update {  currentPosInMillis?.div(1000.0) ?: 0.0 }
                if (_currentTime.value >= totalTime.value) {
                    // Notify next track
                }
                delay(1000)
            }
        }
    }
}
