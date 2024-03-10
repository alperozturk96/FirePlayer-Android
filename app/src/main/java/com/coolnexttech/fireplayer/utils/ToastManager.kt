package com.coolnexttech.fireplayer.utils

import android.annotation.SuppressLint
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.appContext
import com.coolnexttech.fireplayer.utils.extensions.showToast
import java.text.SimpleDateFormat
import java.util.Date

object ToastManager {

    fun showSuccessImportPlaylistMessage() {
        appContext.get()?.showToast(R.string.user_storage_import_success_message)
    }

    fun showFailImportPlaylistMessage() {
        appContext.get()?.showToast(R.string.user_storage_import_fail_message)
    }

    fun showSaveTrackPlaybackPositionMessage() {
        appContext.get()
            ?.showToast(R.string.user_storage_save_current_track_position_success_message)
    }

    fun showEmptyPlaybackMessage() {
        appContext.get()
            ?.showToast(R.string.playlist_empty_message)
    }

    fun showRemoveTrackPlaybackPosition() {
        appContext.get()
            ?.showToast(R.string.user_storage_reset_current_track_position_success_message)
    }

    @SuppressLint("SimpleDateFormat")
    fun showReadTrackPlaybackPositionMessage(result: Long) {
        val time = SimpleDateFormat("mm:ss").format(Date(result))
        appContext.get()?.getString(
            R.string.user_storage_read_current_track_position_success_message,
            time
        )?.let {
            appContext.get()?.showToast(it)
        }
    }

    fun showPlaybackErrorMessage() {
        appContext.get()?.getString(
            R.string.playback_error_message,
        )?.let {
            appContext.get()?.showToast(it)
        }
    }

    fun showDeleteSuccessMessage() {
        appContext.get()?.getString(
            R.string.track_successfully_deleted_message,
        )?.let {
            appContext.get()?.showToast(it)
        }
    }

    fun showSuccessExportPlaylistMessage() {
        appContext.get()?.getString(
            R.string.user_storage_export_success_message,
        )?.let {
            appContext.get()?.showToast(it)
        }
    }

    fun showFailExportSelectedFolderPathMessage() {
        appContext.get()?.showToast(R.string.user_storage_export_fail_to_read_selected_folder_path_message)
    }

    fun showFailExportPlaylistMessage() {
        appContext.get()?.showToast(R.string.user_storage_export_fail_message)
    }
}