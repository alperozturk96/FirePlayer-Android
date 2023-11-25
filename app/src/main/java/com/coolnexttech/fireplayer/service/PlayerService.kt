package com.coolnexttech.fireplayer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel

class PlayerService : Service() {
    private lateinit var viewModel: AudioPlayerViewModel

    override fun onBind(intent: Intent?): IBinder? {
        return null // Return null for started service
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Handle cleanup if needed, e.g., release media player resources
    }
}