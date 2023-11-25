package com.coolnexttech.fireplayer.app

import android.os.Bundle
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

    private fun checkPermissions() {
        permissionManager.askStoragePermission()
        permissionManager.askNotificationPermission()
    }
}
