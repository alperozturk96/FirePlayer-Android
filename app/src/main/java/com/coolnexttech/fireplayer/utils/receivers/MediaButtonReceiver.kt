package com.coolnexttech.fireplayer.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.KeyEvent
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.service.PlayerService

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_MEDIA_BUTTON == intent.action) {

            val keyEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
            }

            Log.d("","KeyEvent: " + keyEvent)

            val playerService = Intent(context, PlayerService::class.java).apply {
                action = when (keyEvent?.action) {
                    KeyEvent.KEYCODE_B -> {
                        PlayerEvents.Toggle.name
                    }
                    KeyEvent.ACTION_UP -> {
                        PlayerEvents.Next.name
                    }
                    KeyEvent.ACTION_DOWN -> {
                        PlayerEvents.Previous.name
                    }
                    else -> { "" }
                }
            }
            context.startForegroundService(playerService)
        }
    }
}