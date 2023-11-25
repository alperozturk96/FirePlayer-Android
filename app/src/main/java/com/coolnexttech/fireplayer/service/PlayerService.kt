package com.coolnexttech.fireplayer.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.createNextTrackPendingIntent
import com.coolnexttech.fireplayer.extensions.createPreviousTrackPendingIntent
import com.coolnexttech.fireplayer.extensions.createTogglePlayerPendingIntent
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.viewModel.ViewModelProvider

class PlayerService : Service() {
    private var homeViewModel = ViewModelProvider.getHomeViewModel()
    private var audioPlayerViewModel = ViewModelProvider.getAudioPlayerViewModel()

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
                homeViewModel.selectPreviousTrack()
                updateNotification()
            }

            PlayerEvents.Toggle.name -> {
                audioPlayerViewModel.togglePlayPause()
            }

            PlayerEvents.Next.name -> {
                homeViewModel.selectNextTrack()
                updateNotification()
            }
        }

        updateNotification()
        return START_STICKY
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(
            notificationId,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    private fun createNotification(): Notification {
        val previousTrackIntent = createPreviousTrackPendingIntent()
        val toggleTrackIntent = createTogglePlayerPendingIntent()
        val nextTrackIntent = createNextTrackPendingIntent()

        return NotificationCompat.Builder(this, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(homeViewModel.currentTrackTitle())
            .setSmallIcon(R.drawable.im_app_icon)
            .addAction(
                R.drawable.ic_previous,
                getString(R.string.media_control_previous_text),
                previousTrackIntent
            )
            .addAction(
                audioPlayerViewModel.toggleIconId(),
                getString(audioPlayerViewModel.toggleIconTextId()),
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
