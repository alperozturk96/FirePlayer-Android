package com.coolnexttech.fireplayer.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.helper.MediaSessionForwardingPlayer
import com.coolnexttech.fireplayer.player.notification.PlayerNotificationManager
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.utils.ToastManager
import com.coolnexttech.fireplayer.utils.UserStorage
import com.coolnexttech.fireplayer.utils.extensions.play
import com.coolnexttech.fireplayer.utils.extensions.startPlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
class AudioPlayer(context: Context?, private val homeViewModel: HomeViewModel): ViewModel() {

    private val tag = "AudioPlayer"

    var mediaSession: MediaSession? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentTime = MutableStateFlow(0L)
    val currentTime: StateFlow<Long> = _currentTime

    private val _totalTime = MutableStateFlow(2L)
    val totalTime: StateFlow<Long> = _totalTime

    private var periodicUpdateJob: Job? = null

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var notificationManager: PlayerNotificationManager
    private var currentTrackIdForSavedTrackPosition: Long? = null

    private val playerAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()

    init {
        context?.let {
            val renderersFactory = DefaultRenderersFactory(context)
            renderersFactory.setEnableAudioFloatOutput(true)

            val player = ExoPlayer.Builder(context, renderersFactory).build().apply {
                volume = 1.0f
                setAudioAttributes(playerAttributes, true)
                setHandleAudioBecomingNoisy(true)
                setWakeMode(C.WAKE_MODE_LOCAL)
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
                            checkSavedTrackPosition()
                            homeViewModel.playNextTrack()
                        }
                    }
                })
            }

            val forwardingPlayer = MediaSessionForwardingPlayer(
                player,
                { homeViewModel.playPreviousTrack() },
                { homeViewModel.playNextTrack() }
            )

            mediaSession = MediaSession.Builder(context, forwardingPlayer)
                .build()

            notificationManager = PlayerNotificationManager(context, player)
        }
    }

    fun startService(service: MediaSessionService) {
        notificationManager.startService(service, mediaSession)
    }

    fun play(track: Track, onSuccess: () -> Unit, onFailure: () -> Unit) {
        try {
            mediaSession.play(track)

            val savedTrackPosition = UserStorage.readTrackPlaybackPosition(track.id, true)
            if (savedTrackPosition != null) {
                currentTrackIdForSavedTrackPosition = track.id
                seekTo(savedTrackPosition)
            }

            appContext.get()?.startPlayerService()
            onSuccess()
        } catch (e: Exception) {
            ToastManager.showPlaybackErrorMessage()
            Log.d(tag, "Error caught at play: $e")
            onFailure()
        }
    }

    private fun checkSavedTrackPosition() {
        currentTrackIdForSavedTrackPosition?.let {
            UserStorage.removeTrackPlaybackPosition(it)
        }
    }

    fun handlePlayerEvent(action: String?) {
        when (action) {
            PlayerEvents.Play.name -> {
                start()
            }
            PlayerEvents.Pause.name -> {
               pause()
            }
            PlayerEvents.Toggle.name -> {
               toggle()
            }
        }
    }

    fun toggle() {
        mediaSession?.player?.apply {
            if (isPlaying) pause() else start()
            _isPlaying.update { isPlaying }
        }
    }

    fun pause() {
        mediaSession?.player?.pause()
        _isPlaying.update { false }
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

    fun seekForward() {
        seekTo(_currentTime.value + 15000)
    }

    fun seekBackward() {
        seekTo(_currentTime.value - 15000)
    }

    fun saveCurrentTrackPlaybackPosition() {
        val currentTrackId = homeViewModel.selectedTrack.value?.id
        val currentTrackPosition = mediaSession?.player?.currentPosition
        UserStorage.saveTrackPlaybackPosition(currentTrackId, currentTrackPosition)
    }

    fun resetCurrentTrackPlaybackPosition() {
        val currentTrackId = homeViewModel.selectedTrack.value?.id ?: return
        UserStorage.removeTrackPlaybackPosition(currentTrackId)
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
