package com.coolnexttech.fireplayer.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.coolnexttech.fireplayer.service.PlayerService

class FirePlayer: Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            PlayerService.channelId,
            "MediaControl",
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = "Used for the media control"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}