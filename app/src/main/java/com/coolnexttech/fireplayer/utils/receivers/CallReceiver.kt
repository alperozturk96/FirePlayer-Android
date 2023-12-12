package com.coolnexttech.fireplayer.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.coolnexttech.fireplayer.utils.VMProvider

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (TelephonyManager.ACTION_PHONE_STATE_CHANGED == intent.action) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                VMProvider.audioPlayer.pause()
            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                VMProvider.audioPlayer.start()
            }
        }
    }
}