package com.coolnexttech.fireplayer.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.coolnexttech.fireplayer.ui.navigation.NavHostScreen
import com.coolnexttech.fireplayer.ui.theme.FirePlayerTheme
import com.coolnexttech.fireplayer.util.CallReceiver
import com.coolnexttech.fireplayer.util.MediaButtonReceiver
import com.coolnexttech.fireplayer.util.PermissionManager

class MainActivity : ComponentActivity() {
    private val permissionManager = PermissionManager(this)
    private val callReceiver = CallReceiver()
    private val mediaButtonReceiver = MediaButtonReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LaunchedEffect(Unit) {
                checkPermissions()
                keepScreenOn()
                acquireWakeLock(this@MainActivity)
                registerCallReceiver()
            }

            FirePlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHostScreen()
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        val mediaButtonFilter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mediaButtonReceiver, mediaButtonFilter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(mediaButtonReceiver, mediaButtonFilter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mediaButtonReceiver)
        unregisterReceiver(callReceiver)
    }

    private fun registerCallReceiver() {
        val filter = IntentFilter(Intent.ACTION_CALL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(callReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(callReceiver, filter)
        }
    }

    private fun acquireWakeLock(context: Context) {
        val wakeLock = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock.run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FirePlayer::WakeLock").apply {
                acquire(60 * 60 * 1000L)
            }
        }
    }

    private fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun checkPermissions() {
        permissionManager.askStoragePermission()
        permissionManager.askNotificationPermission()
    }
}
