package com.coolnexttech.fireplayer.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.utils.extensions.showToast

class PermissionManager(private val activity: ComponentActivity) {

    private val phoneStateManager = PhoneStateManager(activity)

    private val storagePermissionCode = 44

    private fun initHomeViewModel() {
        VMProvider.homeViewModel.init()
    }

    private val storageActivityResultLauncher: ActivityResultLauncher<Intent> =
        activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    initHomeViewModel()
                }
            } else {
                if (it.resultCode == storagePermissionCode) {
                    initHomeViewModel()
                }
            }
        }

    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val writePermission = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED
            val readPermission = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PermissionChecker.PERMISSION_GRANTED

            return writePermission && readPermission
        }
    }

    fun requestForStoragePermissions() {
        if (checkStoragePermissions()) {
            initHomeViewModel()
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent().apply {
                    action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    data = Uri.fromParts("package", activity.packageName, null)
                }
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent().apply {
                    setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                }
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                storagePermissionCode
            )
        }
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
