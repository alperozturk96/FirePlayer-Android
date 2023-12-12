package com.coolnexttech.fireplayer.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.createNextTrackPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createPreviousTrackPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createReturnToAppPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createTogglePlayerPendingIntent

class PlayerService : Service() {
    private val previousTrackIntent: PendingIntent by lazy { createPreviousTrackPendingIntent() }
    private val toggleTrackIntent: PendingIntent by lazy { createTogglePlayerPendingIntent() }
    private val nextTrackIntent: PendingIntent by lazy { createNextTrackPendingIntent() }
    private val returnToAppIntent: PendingIntent by lazy { createReturnToAppPendingIntent() }

    companion object {
        const val notificationId = 1
        const val channelId = "MediaControlChannel"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            PlayerEvents.Previous.name -> {
                VMProvider.homeViewModel.playPreviousTrack()
                updateNotification()
            }

            PlayerEvents.Play.name -> {
                VMProvider.audioPlayer.start()
            }

            PlayerEvents.Pause.name -> {
                VMProvider.audioPlayer.pause()
            }

            PlayerEvents.Toggle.name -> {
                VMProvider.audioPlayer.togglePlayPause()
            }

            PlayerEvents.Next.name -> {
                VMProvider.homeViewModel.playNextTrack()
                updateNotification()
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
                getString(VMProvider.audioPlayer.toggleIconTextId()),
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
