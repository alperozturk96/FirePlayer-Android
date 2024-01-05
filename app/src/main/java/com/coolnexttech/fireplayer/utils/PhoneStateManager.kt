@file:Suppress("DEPRECATION")

package com.coolnexttech.fireplayer.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyCallback
import android.telephony.TelephonyManager

class PhoneStateManager(private val activity: Activity) {

    private val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun listen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.registerTelephonyCallback(
                activity.mainExecutor,
                object : TelephonyCallback(), TelephonyCallback.CallStateListener {
                    override fun onCallStateChanged(state: Int) {
                        handlePhoneState(state)
                    }
                })
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.listen(object : PhoneStateListener() {
                @Deprecated("Deprecated in Java")
                override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                    handlePhoneState(state)
                }
            }, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun handlePhoneState(state: Int) {
        when (state) {
            TelephonyManager.CALL_STATE_RINGING -> {
                VMProvider.audioPlayer.pause()
            }
            TelephonyManager.CALL_STATE_IDLE -> {
                VMProvider.audioPlayer.start()
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                VMProvider.audioPlayer.pause()
            }
        }
    }
}
