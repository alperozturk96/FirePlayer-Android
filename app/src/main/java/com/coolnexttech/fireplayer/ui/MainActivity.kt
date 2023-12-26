package com.coolnexttech.fireplayer.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.coolnexttech.fireplayer.ui.navigation.NavHostScreen
import com.coolnexttech.fireplayer.ui.theme.FirePlayerTheme
import com.coolnexttech.fireplayer.utils.PermissionManager
import com.coolnexttech.fireplayer.utils.receivers.CallReceiver
import com.coolnexttech.fireplayer.utils.receivers.MediaButtonReceiver

class MainActivity : ComponentActivity() {
    private val permissionManager = PermissionManager(this)
    private val callReceiver = CallReceiver()
    private val mediaButtonReceiver = MediaButtonReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            LaunchedEffect(Unit) {
                checkPermissions()
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

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionManager.askStoragePermission()
        }

        permissionManager.askNotificationPermission()
    }
}
