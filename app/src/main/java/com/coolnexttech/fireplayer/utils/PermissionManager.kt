package com.coolnexttech.fireplayer.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.utils.extensions.showToast

class PermissionManager(private val activity: ComponentActivity) {

    private val phoneStateManager = PhoneStateManager(activity)

    @RequiresApi(Build.VERSION_CODES.R)
    fun askStoragePermission() {
        if (Environment.isExternalStorageManager()) {
            return
        }

        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }

    private val phoneStatePermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                phoneStateManager.listen()
            } else {
                showReadPhoneStateInfoMessage()
            }
        }

    fun requestReadPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            showReadPhoneStateInfoMessage()
            phoneStatePermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        } else {
            phoneStateManager.listen()
        }
    }

    private fun showReadPhoneStateInfoMessage() {
        activity.showToast(activity.getString(R.string.read_phone_state_permission_info_message))
    }
}
