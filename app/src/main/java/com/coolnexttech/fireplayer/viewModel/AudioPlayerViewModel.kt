package com.coolnexttech.fireplayer.viewModel

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
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
            prepare() // Use prepareAsync() with OnPreparedListener if you're streaming online media
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

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            _isPlaying.update {
                false
            }
            stopPeriodicUpdateJob()
        }
    }

    fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        } else {
            resume()
        }
    }

    fun updateCurrentTime(value: Double) {
        _currentTime.update {
            value
        }
    }

    fun resume() {
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
            _isPlaying.update {
                true
            }
            startPeriodicUpdateJob()
        }
    }

    fun seekTo(time: Double) {
        mediaPlayer?.let {
            val timeInMillis = (time * 1000).toInt()
            it.seekTo(timeInMillis)
            _currentTime.update {
                time
            }
        }
    }

    private fun startPeriodicUpdateJob() {
        periodicUpdateJob?.cancel()
        periodicUpdateJob = coroutineScope.launch {
            while (isActive) {
                val currentPosInMillis = mediaPlayer?.currentPosition
                _currentTime.update {  currentPosInMillis?.div(1000.0) ?: 0.0 }
                delay(100) // Update the current time every 100 milliseconds
            }
        }
    }

    private fun stopPeriodicUpdateJob() {
        periodicUpdateJob?.cancel()
    }

    fun release() {
        coroutineScope.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}