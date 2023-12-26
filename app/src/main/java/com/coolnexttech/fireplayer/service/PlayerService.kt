package com.coolnexttech.fireplayer.service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.ui.notification.MediaSessionNotificationManager
import com.coolnexttech.fireplayer.utils.VMProvider

class PlayerService : MediaSessionService() {
    private var isPlaying = true
    private val notificationManager = MediaSessionNotificationManager(this)

    companion object {
        const val notificationId = 1
        const val channelId = "MediaControlChannel"
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return VMProvider.audioPlayer.mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = VMProvider.audioPlayer.mediaSession?.player
        if (player?.playWhenReady == false || player?.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            PlayerEvents.Previous.name -> {
                VMProvider.homeViewModel.playPreviousTrack()
            }

            PlayerEvents.Play.name -> {
                VMProvider.audioPlayer.start()
            }

            PlayerEvents.Pause.name -> {
                VMProvider.audioPlayer.pause()
                isPlaying = false
            }

            PlayerEvents.Toggle.name -> {
                VMProvider.audioPlayer.togglePlayPause()
                isPlaying = !isPlaying
            }

            PlayerEvents.Next.name -> {
                VMProvider.homeViewModel.playNextTrack()
            }
        }

        updateNotification()
        return START_STICKY
    }

    private fun updateNotification() {
        val notification = notificationManager.createNotification(isPlaying)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(
                notificationId,
                notification
            )
        }
    }
}
