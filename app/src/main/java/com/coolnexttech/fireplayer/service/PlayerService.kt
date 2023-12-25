package com.coolnexttech.fireplayer.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.createNextTrackPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createPreviousTrackPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createReturnToAppPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createTogglePlayerPendingIntent

class PlayerService : MediaSessionService() {
    private val previousTrackIntent: PendingIntent by lazy { createPreviousTrackPendingIntent() }
    private val toggleTrackIntent: PendingIntent by lazy { createTogglePlayerPendingIntent() }
    private val nextTrackIntent: PendingIntent by lazy { createNextTrackPendingIntent() }
    private val returnToAppIntent: PendingIntent by lazy { createReturnToAppPendingIntent() }

    private var isPlaying = true

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

    override fun onDestroy() {
        VMProvider.audioPlayer.mediaSession?.run {
            player.release()
            release()
            VMProvider.audioPlayer.mediaSession = null
        }
        super.onDestroy()
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
        val notification = createNotification()

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

    private fun createNotification(): Notification {
        val toggleTextId = if (isPlaying) {
            R.string.media_control_pause_text
        } else {
            R.string.media_control_play_text
        }

        return NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(VMProvider.homeViewModel.currentTrackTitle())
            .setContentIntent(returnToAppIntent)
            .setSmallIcon(R.drawable.ic_fire)
            .addAction(
                R.drawable.ic_previous,
                getString(R.string.media_control_previous_text),
                previousTrackIntent
            )
            .addAction(
                R.drawable.ic_pause,
                getString(toggleTextId),
                toggleTrackIntent
            )
            .addAction(
                R.drawable.ic_next,
                getString(R.string.media_control_next_text),
                nextTrackIntent
            )
            .setSilent(true)
            .build()
    }
}
