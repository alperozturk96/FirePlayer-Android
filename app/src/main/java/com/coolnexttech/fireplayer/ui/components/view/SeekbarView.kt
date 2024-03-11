package com.coolnexttech.fireplayer.ui.components.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.HSpacing8
import com.coolnexttech.fireplayer.utils.extensions.convertToReadableTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeekbarView(
    track: Track,
    isTotalTimeValid: Boolean,
    currentTime: Long,
    totalTime: Long,
    isPlaying: Boolean,
    seekTo: (Long) -> Unit,
    updateCurrentTime: (Long) -> Unit,
    toggle: () -> Unit,
    seekBackward: () -> Unit,
    seekForward: () -> Unit,
    playPreviousTrack: () -> Unit,
    playNextTrack: () -> Unit,
    showTrackPositionAlertDialog: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(color = AppColors.textColor, modifier = Modifier.padding(bottom = 8.dp))

        Text(
            text = track.titleRepresentation(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .basicMarquee()
        )

        if (isTotalTimeValid) {
            MediaSlider(
                currentTime,
                totalTime,
                showTrackPositionAlertDialog = { showTrackPositionAlertDialog() },
                seekTo = {
                    seekTo(it)
                },
                updateCurrentTime = {
                    updateCurrentTime(it)
                }
            )
        } else {
            HeadlineSmallText(
                R.string.seek_bar_media_slider_not_available_text,
                color = AppColors.unhighlight
            )
        }

        MediaControl(
            isPlaying = isPlaying,
            toggle = { toggle() },
            seekBackward = { seekBackward() },
            seekForward = { seekForward() },
            playPreviousTrack = { playPreviousTrack() },
            playNextTrack = { playNextTrack() }
        )
    }
}

@Composable
private fun MediaSlider(
    currentTime: Long,
    totalTime: Long,
    updateCurrentTime: (Long) -> Unit,
    seekTo: (Long) -> Unit,
    showTrackPositionAlertDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            currentTime.convertToReadableTime(),
            modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .clickable { showTrackPositionAlertDialog() },
            color = AppColors.unhighlight
        )

        HSpacing8()

        Slider(
            colors = SliderDefaults.colors(
                activeTrackColor = AppColors.red,
            ),
            value = currentTime.toFloat(),
            onValueChange = { newPosition ->
                updateCurrentTime(newPosition.toLong())
            },
            onValueChangeFinished = {
                seekTo(currentTime)
            },
            valueRange = 0f..totalTime.toFloat(),
            modifier = Modifier.weight(0.85f)
        )

        HSpacing8()

        Text(
            totalTime.convertToReadableTime(),
            modifier = Modifier.wrapContentWidth(Alignment.End),
            color = AppColors.unhighlight
        )
    }
}

@Composable
private fun MediaControl(
    isPlaying: Boolean,
    toggle: () -> Unit,
    seekBackward: () -> Unit,
    seekForward: () -> Unit,
    playPreviousTrack: () -> Unit,
    playNextTrack: () -> Unit
) {
    val actions = listOf(
        Pair(R.drawable.ic_fast_rewind) {
            seekBackward()
        },
        Pair(R.drawable.ic_previous) { playPreviousTrack() },
        Pair(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play) { toggle() },
        Pair(R.drawable.ic_next) { playNextTrack() },
        Pair(R.drawable.ic_fast_forward) {
            seekForward()
        },
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        actions.forEach {
            FilledTonalIconButton(
                onClick = { it.second() },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = it.first),
                    contentDescription = "action"
                )
            }
        }
    }
}
