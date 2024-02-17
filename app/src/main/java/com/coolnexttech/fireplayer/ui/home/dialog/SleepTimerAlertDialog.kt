package com.coolnexttech.fireplayer.ui.home.dialog

import android.os.Handler
import android.os.Looper
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.utils.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun SleepTimerAlertDialog(
    audioPlayer: AudioPlayer,
    showSleepTimerAlertDialog: MutableState<Boolean>,
    showTrackActionsBottomSheet: MutableState<Boolean>,
) {
    val context = LocalContext.current
    var sleepTimerDuration by remember { mutableFloatStateOf(1f) }

    if (showSleepTimerAlertDialog.value) {
        val description = stringResource(
            id = R.string.sleep_timer_alert_dialog_description,
            sleepTimerDuration.toInt().toString()
        )

        SimpleAlertDialog(
            titleId = R.string.sleep_timer_alert_dialog_title,
            description = description,
            content = {
                Slider(
                    value = sleepTimerDuration,
                    valueRange = 1f..60f,
                    onValueChange = { sleepTimerDuration = it }
                )
            },
            onComplete = {
                context.showToast(description)
                startSleepTimer(sleepTimerDuration.toInt()) {
                    audioPlayer.pause()
                }
            },
            dismiss = {
                showTrackActionsBottomSheet.value = false
                showSleepTimerAlertDialog.value = false
            }
        )
    }
}

private fun startSleepTimer(minute: Int, onComplete: () -> Unit) {
    val job = CoroutineScope(Dispatchers.IO)
    var jobDurationInSecond = minute * 60

    job.launch {
        while (job.isActive) {
            if (jobDurationInSecond == 1) {
                job.cancel()
            }

            delay(1000)
            jobDurationInSecond -= 1
        }
    }.invokeOnCompletion {
        Handler(Looper.getMainLooper()).postDelayed({
            onComplete()
        }, 100)
    }
}
