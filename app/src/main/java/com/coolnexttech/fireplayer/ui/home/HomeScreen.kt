package com.coolnexttech.fireplayer.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.view.SeekbarView
import com.coolnexttech.fireplayer.ui.home.bottomSheet.TrackActionsBottomSheet
import com.coolnexttech.fireplayer.ui.home.dialog.AddTrackToPlaylistDialog
import com.coolnexttech.fireplayer.ui.home.dialog.DeleteTrackAlertDialog
import com.coolnexttech.fireplayer.ui.home.dialog.SleepTimerAlertDialog
import com.coolnexttech.fireplayer.ui.home.dialog.SortOptionsAlertDialog
import com.coolnexttech.fireplayer.ui.home.topbar.HomeTopBar
import com.coolnexttech.fireplayer.ui.home.trackList.EmptyTrackList
import com.coolnexttech.fireplayer.ui.home.trackList.TrackList
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel

@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    playlistsViewModel: PlaylistsViewModel,
    audioPlayer: AudioPlayer,
) {
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val selectedTrack by viewModel.selectedTrack.collectAsState()

    val showSortOptions = remember { mutableStateOf(false) }

    val selectedTrackForTrackAction = remember { mutableStateOf<Track?>(null) }
    val showTrackActionsBottomSheet = remember { mutableStateOf(false) }

    val showSleepTimerAlertDialog = remember { mutableStateOf(false) }
    val showAddTrackToPlaylistDialog = remember { mutableStateOf(false) }
    val showDeleteTrackAlertDialog = remember { mutableStateOf(false) }
    val showTrackPositionAlertDialog = remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val characterList = remember(filteredTracks) {
        filteredTracks.groupBy { it.title.first().uppercaseChar() }
            .mapValues { (_, tracks) -> filteredTracks.indexOf(tracks.first()) }
    }

    BackHandler {
        if (viewModel.isTracksFiltered()) {
            viewModel.reset()
        }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                viewModel,
                characterList,
                coroutineScope,
                listState,
                showSortOptions = { showSortOptions.value = true },
                showSleepTimerAlertDialog = { showSleepTimerAlertDialog.value = true }
            )
        }, bottomBar = {
            selectedTrack?.let {
                SeekbarView(
                    it,
                    audioPlayer,
                    viewModel,
                    showTrackPositionAlertDialog
                )
            }
        }) {
        if (filteredTracks.isEmpty()) {
            EmptyTrackList(viewModel)
        } else {
            TrackList(
                listState,
                it,
                filteredTracks,
                selectedTrack,
                coroutineScope,
                viewModel,
                selectedTrackForTrackAction,
                showTrackActionsBottomSheet
            )

            TrackActionsBottomSheet(
                audioPlayer,
                selectedTrackForTrackAction,
                showDeleteTrackAlertDialog,
                showTrackActionsBottomSheet,
                showAddTrackToPlaylistDialog
            )

            if (showSortOptions.value) {
                SortOptionsAlertDialog(
                    dismiss = { showSortOptions.value = false },
                    sort = { sortOption ->
                        viewModel.sort(sortOption)
                        showTrackActionsBottomSheet.value = false
                        showSortOptions.value = false
                    }
                )
            }

            AddTrackToPlaylistDialog(
                showAddTrackToPlaylistDialog,
                showTrackActionsBottomSheet,
                selectedTrackForTrackAction,
                playlistsViewModel,
                it
            )

            DeleteTrackAlertDialog(
                viewModel,
                showDeleteTrackAlertDialog,
                showTrackActionsBottomSheet,
                selectedTrackForTrackAction,
                selectedTrack
            )

            SleepTimerAlertDialog(
                audioPlayer,
                showSleepTimerAlertDialog,
                showTrackActionsBottomSheet
            )
        }
    }
}
