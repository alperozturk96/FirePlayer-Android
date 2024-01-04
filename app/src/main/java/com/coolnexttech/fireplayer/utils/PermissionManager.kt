package com.coolnexttech.fireplayer.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi

class PermissionManager(private val activity: Activity) {

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
}
