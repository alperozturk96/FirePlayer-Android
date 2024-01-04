package com.coolnexttech.fireplayer.player.notification

import android.app.PendingIntent
import android.graphics.Bitmap
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager

@UnstableApi
class PlayerNotificationAdapter(
    private val pendingIntent: PendingIntent?,
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): String {
        return player.mediaMetadata.albumTitle.toString()
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        return pendingIntent
    }

    override fun getCurrentContentText(player: Player): String {
        return player.mediaMetadata.displayTitle.toString()
    }

    override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
        return null
    }
}
