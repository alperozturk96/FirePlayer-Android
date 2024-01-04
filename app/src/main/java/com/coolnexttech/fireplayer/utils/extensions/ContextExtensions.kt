package com.coolnexttech.fireplayer.utils.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.coolnexttech.fireplayer.player.service.PlayerService
import com.coolnexttech.fireplayer.ui.MainActivity

fun Context.startPlayerService() {
    val intent = Intent(this, PlayerService::class.java)
    startForegroundService(intent)
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(messageId: Int) {
    Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
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
