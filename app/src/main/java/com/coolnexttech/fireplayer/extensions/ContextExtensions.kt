package com.coolnexttech.fireplayer.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.coolnexttech.fireplayer.model.PlayerEvents
import com.coolnexttech.fireplayer.service.PlayerService

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