package com.coolnexttech.fireplayer.util

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.telephony.TelephonyManager
import com.coolnexttech.fireplayer.viewModel.ViewModelProvider

class CallReceiver : BroadcastReceiver() {
    private val audioPlayerViewModel = ViewModelProvider.getAudioPlayerViewModel()

    // FIXME not working
    override fun onReceive(context: Context, intent: Intent) {
        acquireWakeLock(context)

        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                audioPlayerViewModel.pause()
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                audioPlayerViewModel.start()
            }
        }
    }

    @SuppressLint("WakelockTimeout")
    private fun acquireWakeLock(context: Context) {
        val wakeLock = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock.run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FirePlayer::CallReceiverWakeLock").apply {
                acquire()
            }
        }
    }
}