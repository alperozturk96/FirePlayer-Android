package com.coolnexttech.fireplayer.utils

import android.Manifest
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.utils.extensions.showToast

class PermissionManager(private val activity: ComponentActivity) {

    private val phoneStateManager = PhoneStateManager(activity)

    private val storagePermissionCode = 44

    private val requestPermissions = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (results[READ_MEDIA_AUDIO] == true) {
            initHomeViewModel()
        }
    }

    private fun initHomeViewModel() {
        VMProvider.homeViewModel.init()
    }

    private fun checkStoragePermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity, READ_MEDIA_AUDIO
            ) == PermissionChecker.PERMISSION_GRANTED
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(arrayOf(READ_MEDIA_AUDIO))
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
