package com.coolnexttech.fireplayer.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val activity: Activity) {

    private val externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
    private val requestReadExternalStorageCode = 101

    fun askStoragePermission() {
        if (ContextCompat.checkSelfPermission(activity, externalStoragePermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestReadExternalStorageCode)
        }
    }
}
