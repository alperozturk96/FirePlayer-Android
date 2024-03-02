package com.coolnexttech.fireplayer.ui.components.view

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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.HSpacing8
import com.coolnexttech.fireplayer.utils.extensions.VSpacing4
import com.coolnexttech.fireplayer.utils.extensions.convertToReadableTime

@Composable
fun SeekbarView(
    selectedTrack: Track,
    audioPlayer: AudioPlayer,
    homeViewModel: HomeViewModel,
    showTrackPositionAlertDialog: MutableState<Boolean>
) {
    val currentTime by audioPlayer.currentTime.collectAsState()
    val totalTime by audioPlayer.totalTime.collectAsState()
    val isPlaying by audioPlayer.isPlaying.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VSpacing4()

        HorizontalDivider(color = AppColors.textColor)

        VSpacing4()

        Text(
            text = selectedTrack.titleRepresentation(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        if (audioPlayer.isTotalTimeValid()) {
            MediaSlider(audioPlayer, currentTime, totalTime, showTrackPositionAlertDialog)
        } else {
            MediaSliderNotAvailable()
        }

        MediaControl(audioPlayer, isPlaying, { homeViewModel.playPreviousTrack() }) {
            homeViewModel.playNextTrack()
        }
    }
}

@Composable
private fun MediaSliderNotAvailable() {
    HeadlineSmallText(
        R.string.seek_bar_media_slider_not_available_text,
        color = AppColors.unhighlight
    )
}

@Composable
private fun MediaSlider(
    audioPlayer: AudioPlayer,
    currentTime: Long,
    totalTime: Long,
    showTrackPositionAlertDialog: MutableState<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            currentTime.convertToReadableTime(),
            modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .clickable { showTrackPositionAlertDialog.value = true },
            color = AppColors.unhighlight
        )

        HSpacing8()

        Slider(
            colors = SliderDefaults.colors(
                activeTrackColor = AppColors.red,
            ),
            value = currentTime.toFloat(),
            onValueChange = { newPosition ->
                audioPlayer.updateCurrentTime(newPosition.toLong())
            },
            onValueChangeFinished = {
                audioPlayer.seekTo(currentTime)
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
    audioPlayer: AudioPlayer,
    isPlaying: Boolean,
    selectPreviousTrack: () -> Unit,
    selectNextTrack: () -> Unit
) {
    val actions = listOf(
        Pair(R.drawable.ic_fast_rewind) { audioPlayer.seekBackward() },
        Pair(R.drawable.ic_previous) { selectPreviousTrack() },
        Pair(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play) { audioPlayer.togglePlayPause() },
        Pair(R.drawable.ic_next) { selectNextTrack() },
        Pair(R.drawable.ic_fast_forward) { audioPlayer.seekForward() },
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
