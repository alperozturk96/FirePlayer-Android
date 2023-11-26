package com.coolnexttech.fireplayer.app

import android.content.Context
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
import androidx.navigation.compose.rememberNavController
import com.coolnexttech.fireplayer.ui.navigation.Destinations
import com.coolnexttech.fireplayer.ui.navigation.Navigation
import com.coolnexttech.fireplayer.ui.theme.FirePlayerTheme
import com.coolnexttech.fireplayer.util.PermissionManager

class MainActivity : ComponentActivity() {
    private val permissionManager = PermissionManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LaunchedEffect(Unit) {
                checkPermissions()
                keepScreenOn()
                acquireWakeLock(this@MainActivity)
            }

            FirePlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Navigation(navController, startDestination = Destinations.Home)
                }
            }
        }
    }

    private fun acquireWakeLock(context: Context) {
        val wakeLock = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock.run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "FirePlayer::WakeLock").apply {
                acquire(60*60*1000L)
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
