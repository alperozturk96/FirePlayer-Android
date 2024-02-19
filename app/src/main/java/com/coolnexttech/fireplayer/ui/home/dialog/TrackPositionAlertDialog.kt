package com.coolnexttech.fireplayer.ui.home.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.showToast

@Composable
fun TrackPositionAlertDialog(
    audioPlayer: AudioPlayer,
    showTrackPositionAlertDialog: MutableState<Boolean>,
    showTrackActionsBottomSheet: MutableState<Boolean>
) {
    if (showTrackPositionAlertDialog.value) {
        val context = LocalContext.current
        var durationText by remember { mutableStateOf("") }
        val currentTime by audioPlayer.currentTime.collectAsState()
        val totalTime by audioPlayer.totalTime.collectAsState()

        SimpleAlertDialog(
            titleId = R.string.track_position_alert_dialog_title,
            description = null,
            dismiss = {
                showTrackActionsBottomSheet.value = false
                showTrackPositionAlertDialog.value = false
            },
            onComplete = {
                //FIXME
                val duration = convertDurationStringToLong(durationText)
                if (duration != null) {
                    if (duration in currentTime..totalTime) {
                        audioPlayer.seekTo(duration)
                    } else {
                        context.showToast(R.string.track_position_alert_dialog_text_field_not_in_range_error_message)
                    }
                } else {
                    context.showToast(R.string.track_position_alert_dialog_text_field_not_valid_error_message)
                }
            }, content = {
                TextField(
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = AppColors.textColor,
                        focusedContainerColor = AppColors.alternateBackground,
                        unfocusedContainerColor = AppColors.alternateBackground,
                        focusedIndicatorColor = AppColors.textColor,
                        unfocusedIndicatorColor = AppColors.textColor,
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.track_position_alert_dialog_description),
                            color = AppColors.textColor,
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = durationText,
                    onValueChange = {
                        durationText = it
                    },
                    singleLine = true
                )
            }
        )
    }
}

private fun convertDurationStringToLong(durationString: String): Long? {
    val parts = durationString.split(".", ":")

    if (parts.size == 2 || parts.size == 3) {
        return try {
            val hours = parts.getOrNull(0)?.toLong() ?: 0
            val minutes = parts.getOrNull(1)?.toLong() ?: 0
            val seconds = parts.getOrNull(2)?.toLong() ?: 0

            (hours * 3600 + minutes * 60 + seconds) * 1000
        } catch (e: NumberFormatException) {
            null
        }
    }

    return null
}