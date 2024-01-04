package com.coolnexttech.fireplayer.player.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.coolnexttech.fireplayer.player.notification.PlayerNotificationManager
import com.coolnexttech.fireplayer.utils.VMProvider

class PlayerService : MediaSessionService() {

    private var playerNotificationManager: PlayerNotificationManager? = null

    override fun onCreate() {
        playerNotificationManager = PlayerNotificationManager(this, VMProvider.audioPlayer.mediaSession!!.player)
        super.onCreate()
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

    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        VMProvider.audioPlayer.handlePlayerEvent(intent?.action)
        VMProvider.homeViewModel.handlePlayerEvent(intent?.action)
        playerNotificationManager?.startService(
            mediaSession = VMProvider.audioPlayer.mediaSession!!,
            mediaSessionService = this
        )

        return super.onStartCommand(intent, flags, startId)
    }
}
