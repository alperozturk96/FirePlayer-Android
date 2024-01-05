package com.coolnexttech.fireplayer.ui

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

class MainActivity : ComponentActivity() {
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)

        setContent {
            LaunchedEffect(Unit) {
                checkPermissions()
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

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissionManager.askStoragePermission()
        }

        permissionManager.requestReadPhoneStatePermission()
    }
}
