package com.coolnexttech.fireplayer.player.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.utils.extensions.createReturnToAppPendingIntent

class PlayerNotificationManager(
    private val context: Context,
    private val player: Player
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        const val notificationId = 1
        const val channelName = "MediaControl"
        const val channelId = "MediaControlChannel"
    }

    init {
        createChannel()
    }

    @UnstableApi
    fun startService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession?
    ) {
        mediaSession?.let {
            createNotification(mediaSession)
        }

        val notification = Notification.Builder(context, channelId)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        mediaSessionService.startForeground(notificationId, notification)
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)
    }

    @UnstableApi
    private fun createNotification(mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(
            context,
            notificationId,
            channelId
        )
            .setMediaDescriptionAdapter(
                PlayerNotificationAdapter(
                    context.createReturnToAppPendingIntent(),
                )
            )
            .setSmallIconResourceId(R.drawable.ic_fire)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setPlayer(player)
            }
    }
}
