package com.coolnexttech.fireplayer.utils.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.service.PlayerService
import com.coolnexttech.fireplayer.ui.MainActivity

fun Context.startPlayerServiceWithDelay(delay: Long = 1500L) {
    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(this, PlayerService::class.java)
        startForegroundService(intent)
    }, delay)
}

fun Context.createPreviousTrackPendingIntent(): PendingIntent {
    val intent = Intent(this, PlayerService::class.java).apply {
        action = PlayerEvents.Previous.name
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

fun Context.createTogglePlayerPendingIntent(): PendingIntent {
    val intent = Intent(this, PlayerService::class.java).apply {
        action = PlayerEvents.Toggle.name
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}

fun Context.createReturnToAppPendingIntent(): PendingIntent {
    val mainActivityIntent = Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getActivity(this, 0, mainActivityIntent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getActivity(this, 0, mainActivityIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)
    }
}

fun Context.createNextTrackPendingIntent(): PendingIntent {
    val intent = Intent(this, PlayerService::class.java).apply {
        action = PlayerEvents.Next.name
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_MUTABLE)
    } else {
        PendingIntent.getService(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
