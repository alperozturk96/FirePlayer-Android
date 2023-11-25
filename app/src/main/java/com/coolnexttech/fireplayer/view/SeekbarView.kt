package com.coolnexttech.fireplayer.view

import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.HSpacing8
import com.coolnexttech.fireplayer.extensions.convertToReadableTime
import com.coolnexttech.fireplayer.ui.components.ActionButton
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel
import com.coolnexttech.fireplayer.viewModel.HomeViewModel

@Composable
fun SeekbarView(
    audioPlayerViewModel: AudioPlayerViewModel,
    homeViewModel: HomeViewModel,
) {
    val currentTime by audioPlayerViewModel.currentTime.collectAsState()
    val totalTime by audioPlayerViewModel.totalTime.collectAsState()
    val isPlaying by audioPlayerViewModel.isPlaying.collectAsState()

    // When currentTime reaches totalTime, trigger next track
    LaunchedEffect(currentTime, totalTime) {
        if (currentTime != 0.0 && currentTime >= totalTime) {
            homeViewModel.selectNextTrack()
        }
    }

    Column(
        modifier = Modifier
            .background(AppColors.alternateBackground)
            .padding(all = 16.dp)
    ) {

        MediaSlider(audioPlayerViewModel, currentTime, totalTime)
        MediaControl(audioPlayerViewModel, isPlaying, { homeViewModel.selectPreviousTrack() }) {
            homeViewModel.selectNextTrack()
        }
    }
}

@Composable
private fun MediaSlider(
    audioPlayerViewModel: AudioPlayerViewModel,
    currentTime: Double,
    totalTime: Double
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

        // FIXME fix seekto
        Slider(
            colors = SliderDefaults.colors(
                thumbColor = AppColors.unhighlight,
                activeTickColor = AppColors.unhighlight,
                activeTrackColor = AppColors.slider,
                inactiveTickColor = AppColors.unhighlight
            ),
            value = currentTime.toFloat(),
            onValueChange = { newPosition ->
                audioPlayerViewModel.updateCurrentTime(newPosition.toDouble())
            },
            onValueChangeFinished = {
                audioPlayerViewModel.seekTo(currentTime)
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
    audioPlayerViewModel: AudioPlayerViewModel,
    isPlaying: Boolean,
    selectPreviousTrack: () -> Unit,
    selectNextTrack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))

        ActionButton(R.drawable.ic_previous) {
            selectPreviousTrack()
        }

        ActionButton(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play) {
            audioPlayerViewModel.togglePlayPause()
        }

        ActionButton(R.drawable.ic_next) {
            selectNextTrack()
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}
