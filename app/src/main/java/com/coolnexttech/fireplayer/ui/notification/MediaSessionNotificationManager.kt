package com.coolnexttech.fireplayer.ui.notification

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.service.PlayerService
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.createNextTrackPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createPreviousTrackPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createReturnToAppPendingIntent
import com.coolnexttech.fireplayer.utils.extensions.createTogglePlayerPendingIntent

class MediaSessionNotificationManager(private val context: Context) {

    private val previousTrackIntent: PendingIntent by lazy { context.createPreviousTrackPendingIntent() }
    private val toggleTrackIntent: PendingIntent by lazy { context.createTogglePlayerPendingIntent() }
    private val nextTrackIntent: PendingIntent by lazy { context.createNextTrackPendingIntent() }
    private val returnToAppIntent: PendingIntent by lazy { context.createReturnToAppPendingIntent() }

    fun createNotification(isPlaying: Boolean): Notification {
        val toggleTextId = if (isPlaying) {
            R.string.media_control_pause_text
        } else {
            R.string.media_control_play_text
        }

        return NotificationCompat.Builder(context, PlayerService.channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(VMProvider.homeViewModel.currentTrackTitle())
            .setContentText(VMProvider.homeViewModel.currentTrackDetail())
            .setContentIntent(returnToAppIntent)
            .setSmallIcon(R.drawable.ic_fire)
            .addAction(
                R.drawable.ic_previous,
                context.getString(R.string.media_control_previous_text),
                previousTrackIntent
            )
            .addAction(
                R.drawable.ic_pause,
                context.getString(toggleTextId),
                toggleTrackIntent
            )
            .addAction(
                R.drawable.ic_next,
                context.getString(R.string.media_control_next_text),
                nextTrackIntent
            )
            .setSilent(true)
            .build()
    }
}