package com.coolnexttech.fireplayer.ui.components.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.ActionImageButton
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.HSpacing16
import com.coolnexttech.fireplayer.utils.extensions.HSpacing8
import com.coolnexttech.fireplayer.utils.extensions.VSpacing8
import com.coolnexttech.fireplayer.utils.extensions.convertToReadableTime

@Composable
fun SeekbarView(
    selectedTrack: Track,
    audioPlayer: AudioPlayer,
    homeViewModel: HomeViewModel,
) {
    val currentTime by audioPlayer.currentTime.collectAsState()
    val totalTime by audioPlayer.totalTime.collectAsState()
    val isPlaying by audioPlayer.isPlaying.collectAsState()

    Column(
        modifier = Modifier
            .background(AppColors.alternateBackground)
            .padding(all = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = selectedTrack.titleRepresentation(),
            color = AppColors.textColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        VSpacing8()

        if (audioPlayer.isTotalTimeValid()) {
            MediaSlider(audioPlayer, currentTime, totalTime)
        } else {
            MediaSliderNotAvailable()
        }

        VSpacing8()

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
    totalTime: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            currentTime.convertToReadableTime(),
            modifier = Modifier.wrapContentWidth(Alignment.Start),
            color = AppColors.unhighlight
        )

        HSpacing8()

        Slider(
            colors = SliderDefaults.colors(
                thumbColor = AppColors.unhighlight,
                activeTickColor = AppColors.unhighlight,
                activeTrackColor = AppColors.red,
                inactiveTickColor = AppColors.unhighlight
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
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ActionImageButton(R.drawable.ic_previous) {
            selectPreviousTrack()
        }

        HSpacing16()

        ActionImageButton(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play, size = 40.dp) {
            audioPlayer.togglePlayPause()
        }

        HSpacing16()

        ActionImageButton(R.drawable.ic_next) {
            selectNextTrack()
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
