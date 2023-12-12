package com.coolnexttech.fireplayer.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.KeyEvent
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.service.PlayerService

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_MEDIA_BUTTON != intent.action) {
            return
        }

        val keyEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
        }

        val eventName = when (keyEvent?.keyCode) {
            KeyEvent.KEYCODE_MEDIA_NEXT -> PlayerEvents.Next.name
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> PlayerEvents.Previous.name
            KeyEvent.KEYCODE_MEDIA_PAUSE -> PlayerEvents.Pause.name
            KeyEvent.KEYCODE_MEDIA_PLAY -> PlayerEvents.Play.name
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> PlayerEvents.Toggle.name
            else -> { null }
        }

        val playerService = Intent(context, PlayerService::class.java).apply {
            action = eventName
        }

        context.startForegroundService(playerService)
    }
}