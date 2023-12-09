package com.coolnexttech.fireplayer

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.coolnexttech.fireplayer.service.PlayerService

class FirePlayer: Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            PlayerService.channelId,
            "MediaControl",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Used for the media control"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
