package com.coolnexttech.fireplayer.service

import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.coolnexttech.fireplayer.utils.VMProvider

class PlayerService : MediaSessionService() {
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
        VMProvider.audioPlayer.handlePlayerEvent(intent?.action)
        VMProvider.homeViewModel.handlePlayerEvent(intent?.action)
        VMProvider.audioPlayer.startService(this)

        return super.onStartCommand(intent, flags, startId)
    }
}
