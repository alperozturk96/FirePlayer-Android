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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.ui.components.view.SeekbarView
import com.coolnexttech.fireplayer.ui.home.bottomSheet.TrackActionsBottomSheet
import com.coolnexttech.fireplayer.ui.home.dialog.AddTrackToPlaylistDialog
import com.coolnexttech.fireplayer.ui.home.dialog.SleepTimerAlertDialog
import com.coolnexttech.fireplayer.ui.home.dialog.SortOptionsAlertDialog
import com.coolnexttech.fireplayer.ui.home.topbar.HomeTopBar
import com.coolnexttech.fireplayer.ui.home.trackList.EmptyTrackList
import com.coolnexttech.fireplayer.ui.home.trackList.TrackList
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.ToastManager
import com.coolnexttech.fireplayer.utils.extensions.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    playlistsViewModel: PlaylistsViewModel,
    audioPlayer: AudioPlayer,
) {
    val context = LocalContext.current

    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val selectedTrack by viewModel.selectedTrack.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val playlists by playlistsViewModel.playlists.collectAsState()

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
                searchText,
                listState,
                showSortOptions = { showSortOptions.value = true },
                showSleepTimerAlertDialog = { showSleepTimerAlertDialog.value = true }
            )
        }, bottomBar = {
            selectedTrack?.let { track ->
                val currentTime by audioPlayer.currentTime.collectAsState()
                val totalTime by audioPlayer.totalTime.collectAsState()
                val isPlaying by audioPlayer.isPlaying.collectAsState()

                SeekbarView(
                    track,
                    audioPlayer.isTotalTimeValid(),
                    currentTime,
                    totalTime,
                    isPlaying,
                    seekTo = { position ->
                        audioPlayer.seekTo(position)
                    },
                    updateCurrentTime = { time ->
                        audioPlayer.updateCurrentTime(time)
                    },
                    toggle = {
                        audioPlayer.toggle()
                    },
                    seekBackward = {
                        audioPlayer.seekBackward()
                    },
                    seekForward = {
                        audioPlayer.seekForward()
                    },
                    showTrackPositionAlertDialog = {
                        showTrackPositionAlertDialog.value = true
                    },
                    playPreviousTrack = {
                        viewModel.playPreviousTrack()
                    },
                    playNextTrack = {
                        viewModel.playNextTrack()
                    }
                )
            }
        }) {
        if (filteredTracks.isEmpty()) {
            EmptyTrackList(searchText)
        } else {
            TrackList(
                listState,
                it,
                filteredTracks,
                selectedTrack,
                action = { (index, track) ->
                    coroutineScope.launch(Dispatchers.Main) {
                        listState.animateScrollToItem(index)
                    }

                    viewModel.playTrack(track)
                },
                longPressAction = { track ->
                    selectedTrackForTrackAction.value = track
                    showTrackActionsBottomSheet.value = true
                }
            )

            if (showTrackActionsBottomSheet.value) {
                TrackActionsBottomSheet(
                    audioPlayer,
                    selectedTrackForTrackAction.value?.title ?: "",
                    showDeleteTrackAlertDialog = {
                        showDeleteTrackAlertDialog.value = true
                    },
                    dismiss = {
                        showTrackActionsBottomSheet.value = false
                    },
                    showAddTrackToPlaylistDialog = {
                        if (playlists.isNotEmpty()) {
                            showAddTrackToPlaylistDialog.value = true
                        } else {
                            ToastManager.showEmptyPlaybackMessage()
                        }
                    }
                )
            }

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

            if (showAddTrackToPlaylistDialog.value) {
                AddTrackToPlaylistDialog(
                    playlists,
                    it,
                    addToPlaylist = { playlistTitle ->
                        selectedTrackForTrackAction.value?.let { track ->
                            playlistsViewModel.addTrackToPlaylist(
                                track.titleRepresentation(),
                                playlistTitle
                            )

                            context.run {
                                showToast(
                                    getString(
                                        R.string.playlist_screen_add,
                                        track.titleRepresentation(),
                                        playlistTitle
                                    )
                                )
                            }
                        }
                    },
                    dismiss = {
                        showTrackActionsBottomSheet.value = false
                        showAddTrackToPlaylistDialog.value = false
                    }
                )
            }

            if (showDeleteTrackAlertDialog.value) {
                SimpleAlertDialog(
                    titleId = R.string.delete_alert_dialog_title,
                    description = stringResource(R.string.delete_alert_dialog_description),
                    onComplete = {
                        selectedTrackForTrackAction.value?.let { track ->
                            if (track == selectedTrack) {
                                viewModel.playNextTrack()
                            }

                            FolderAnalyzer.deleteTrack(track)
                            viewModel.deleteTrack(track)
                            viewModel.search(searchText)
                            ToastManager.showDeleteSuccessMessage()
                        }
                    },
                    dismiss = {
                        showTrackActionsBottomSheet.value = false
                        showDeleteTrackAlertDialog.value = false
                    }
                )
            }

            if (showSleepTimerAlertDialog.value) {
                SleepTimerAlertDialog(
                    pause = {
                        audioPlayer.pause()
                    },
                    dismiss = {
                        showTrackActionsBottomSheet.value = false
                        showSleepTimerAlertDialog.value = false
                    }
                )
            }
        }
    }
}
