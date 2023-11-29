package com.coolnexttech.fireplayer.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.coolnexttech.fireplayer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class AudioPlayerViewModel(context: Context): ViewModel() {

    private var player: ExoPlayer? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentTime = MutableStateFlow(0L)
    val currentTime: StateFlow<Long> = _currentTime

    private val _totalTime = MutableStateFlow(2L)
    val totalTime: StateFlow<Long> = _totalTime

    private var periodicUpdateJob: Job? = null

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private val homeViewModel = ViewModelProvider.homeViewModel

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    init {
        player = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) {
                        startPeriodicUpdateJob()
                    } else {
                        periodicUpdateJob?.cancel()
                    }
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _totalTime.value = duration
                        _currentTime.value = currentPosition
                    } else if (playbackState == Player.STATE_ENDED) {
                        homeViewModel.selectNextTrack()
                    }
                 }
            })
        }

        player?.setAudioAttributes(audioAttributes, true)
    }

    fun play(uri: Uri) {
        player?.apply {
            stop()
            clearMediaItems()
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        player?.apply {
            if (isPlaying) pause() else start()
            _isPlaying.update { isPlaying }
        }
    }

    fun pause() {
        player?.pause()
    }

    fun stop() {
        player?.stop()
    }

    fun start() {
        player?.play()
    }

    fun updateCurrentTime(value: Long) {
        _currentTime.update {
            value
        }
    }

    fun seekTo(time: Long) {
        player?.let {
            it.seekTo(time)
            _currentTime.value = time
        }
    }

    fun isTotalTimeValid(): Boolean = totalTime.value > 2L

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
            while (true) {
                 delay(1000)
                _currentTime.value = player?.currentPosition ?: 0
                _totalTime.value = player?.duration ?: 0

            }
        }
    }
}
