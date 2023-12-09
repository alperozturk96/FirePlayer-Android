package com.coolnexttech.fireplayer.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.coolnexttech.fireplayer.utils.ViewModelProvider

class CallReceiver : BroadcastReceiver() {
    private val audioPlayerViewModel = ViewModelProvider.audioPlayerViewModel()

    override fun onReceive(context: Context, intent: Intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                audioPlayerViewModel.pause()
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                audioPlayerViewModel.start()
            }
        }
    }
}