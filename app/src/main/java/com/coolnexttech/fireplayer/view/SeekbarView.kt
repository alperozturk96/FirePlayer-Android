package com.coolnexttech.fireplayer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.convertToReadableTime
import com.coolnexttech.fireplayer.ui.components.ActionButton
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel

@Composable
fun SeekbarView(
    audioPlayerViewModel: AudioPlayerViewModel,
    selectPreviousTrack: () -> Unit,
    selectNextTrack: () -> Unit
) {
    val currentTime by audioPlayerViewModel.currentTime.collectAsState()
    val totalTime by audioPlayerViewModel.totalTime.collectAsState()
    val isPlaying by audioPlayerViewModel.isPlaying.collectAsState()

    // When currentTime reaches totalTime, trigger next track
    LaunchedEffect(currentTime, totalTime) {
        if (currentTime != 0.0 && currentTime >= totalTime) {
            selectNextTrack()
        }
    }

    Column(
        modifier = Modifier
            .background(AppColors.alternateBackground)
            .padding(all = 16.dp)
    ) {

        MediaSlider(audioPlayerViewModel, currentTime, totalTime)
        MediaControl(audioPlayerViewModel, isPlaying, selectPreviousTrack = selectPreviousTrack) {
            selectNextTrack()
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
            color = AppColors.unHighlight
        )

        Spacer(Modifier.width(4.dp))

        // FIXME fix seekto
        Slider(
            colors = SliderDefaults.colors(
                thumbColor = AppColors.unHighlight,
                activeTickColor = AppColors.unHighlight,
                activeTrackColor = AppColors.slider,
                inactiveTickColor = AppColors.unHighlight
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

        Spacer(Modifier.width(4.dp))

        Text(
            totalTime.convertToReadableTime(),
            modifier = Modifier.wrapContentWidth(Alignment.End),
            color = AppColors.unHighlight
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
