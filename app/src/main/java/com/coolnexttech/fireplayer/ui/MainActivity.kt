package com.coolnexttech.fireplayer.ui

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

class MainActivity : ComponentActivity() {
    private val permissionManager = PermissionManager(this)
    private val callReceiver = CallReceiver()

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

    override fun onDestroy() {
        super.onDestroy()
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
    }
}
