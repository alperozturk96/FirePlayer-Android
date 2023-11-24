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
import androidx.compose.material3.Text
import com.coolnexttech.fireplayer.extensions.convertToReadableTime
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel

@Composable
fun SeekbarView(
    audioPlayerViewModel: AudioPlayerViewModel,
    selectPreviousTrack: () -> Unit,
    selectNextTrack: () -> Unit
) {
    var isSeeking by remember { mutableStateOf(false) }
    val currentTime by audioPlayerViewModel.currentTime.collectAsState()
    val totalTime by audioPlayerViewModel.totalTime.collectAsState()
    val isPlaying by audioPlayerViewModel.isPlaying.collectAsState()

    // When currentTime reaches totalTime, trigger next track
    LaunchedEffect(currentTime, totalTime) {
        if (currentTime != 0.0 && currentTime >= totalTime) {
            selectNextTrack()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.LightGray) // Replace with your color
    ) {
        Spacer(modifier = Modifier.width(15.dp))

        Text(currentTime.convertToReadableTime())

        Slider(
            value = currentTime.toFloat(),
            onValueChange = { newPosition ->
                isSeeking = true
                audioPlayerViewModel.updateCurrentTime(newPosition.toDouble())
            },
            onValueChangeFinished = {
                isSeeking = false
                audioPlayerViewModel.seekTo(currentTime)
            },
            valueRange = 0f..totalTime.toFloat()
        )

        Text(totalTime.convertToReadableTime())

        Spacer(modifier = Modifier.width(15.dp))

        IconButton(onClick = selectPreviousTrack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Previous")
        }

        IconButton(onClick = { audioPlayerViewModel.togglePlayPause() }) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Place else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play"
            )
        }

        IconButton(onClick = selectNextTrack) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Next")
        }

        Spacer(modifier = Modifier.width(15.dp))
    }
}