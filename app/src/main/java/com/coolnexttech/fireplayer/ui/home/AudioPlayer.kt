package com.coolnexttech.fireplayer.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.coolnexttech.fireplayer.FirePlayer
import com.coolnexttech.fireplayer.utils.VMProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("StaticFieldLeak")
class AudioPlayer(context: Context): ViewModel() {

    var mediaSession: MediaSession? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentTime = MutableStateFlow(0L)
    val currentTime: StateFlow<Long> = _currentTime

    private val _totalTime = MutableStateFlow(2L)
    val totalTime: StateFlow<Long> = _totalTime

    private var periodicUpdateJob: Job? = null

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private val playerAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    init {
        val player = ExoPlayer.Builder(context).build().apply {
            volume = 1.0f
            setAudioAttributes(playerAttributes, true)
            setHandleAudioBecomingNoisy(true)
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
                        VMProvider.homeViewModel.playNextTrack()
                    }
                 }
            })
        }

        mediaSession = MediaSession.Builder(FirePlayer.context, player).build()
    }

    fun play(uri: Uri) {
        try {
            mediaSession?.player?.apply {
                stop()
                clearMediaItems()
                val mediaItem: MediaItem = MediaItem.fromUri(uri)
                setMediaItem(mediaItem)
                prepare()
                play()
            }
        } catch (e: Exception) {
            VMProvider.homeViewModel.playNextTrack()
        }
    }

    fun togglePlayPause() {
        mediaSession?.player?.apply {
            if (isPlaying) pause() else start()
            _isPlaying.update { isPlaying }
        }
    }

    fun pause() {
        mediaSession?.player?.pause()
    }

    fun start() {
        mediaSession?.player?.play()
    }

    fun updateCurrentTime(value: Long) {
        _currentTime.update {
            value
        }
    }

    fun seekTo(time: Long) {
        mediaSession?.player?.let {
            it.seekTo(time)
            _currentTime.value = time
        }
    }

    fun isTotalTimeValid(): Boolean = totalTime.value > 2L

    private fun startPeriodicUpdateJob() {
        periodicUpdateJob?.cancel()
        periodicUpdateJob = coroutineScope.launch {
            while (true) {
                 delay(1000)
                _currentTime.value = mediaSession?.player?.currentPosition ?: 0
                _totalTime.value = mediaSession?.player?.duration ?: 0
            }
        }
    }
}
