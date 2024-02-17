package com.coolnexttech.fireplayer.ui.home

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.bottomSheet.MoreActionsBottomSheet
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.ui.components.dialog.SortOptionsAlertDialog
import com.coolnexttech.fireplayer.ui.components.view.ContentUnavailableView
import com.coolnexttech.fireplayer.ui.components.view.SeekbarView
import com.coolnexttech.fireplayer.ui.home.topbar.HomeTopBar
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.ToastManager
import com.coolnexttech.fireplayer.utils.extensions.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
    val filterOption by viewModel.filterOption.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val showSortOptions = remember { mutableStateOf(false) }
    val showSleepTimerAlertDialog = remember { mutableStateOf(false) }
    val selectedTrackForTrackAction = remember { mutableStateOf<Track?>(null) }
    val showAddToPlaylistDialog = remember { mutableStateOf(false) }
    val showTrackActionsBottomSheet = remember { mutableStateOf(false) }
    val showAlphabeticalScroller = remember { mutableStateOf(false) }
    val playlists by playlistsViewModel.playlists.collectAsState()
    val alphabeticalScrollerIconId = if (showAlphabeticalScroller.value) {
        R.drawable.ic_arrow_up
    } else {
        R.drawable.ic_arrow_down
    }
    val showDeleteTrackDialog = remember { mutableStateOf(false) }
    var sleepTimerDuration by remember { mutableFloatStateOf(1f) }
    val selectedTrack by viewModel.selectedTrack.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val characterList = remember(filteredTracks) {
        filteredTracks.groupBy { it.title.first().uppercaseChar() }
            .mapValues { (_, tracks) -> filteredTracks.indexOf(tracks.first()) }
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                context,
                playMode,
                alphabeticalScrollerIconId,
                filterOption,
                searchText,
                viewModel,
                showAlphabeticalScroller,
                characterList,
                coroutineScope,
                listState,
                showSortOptions = { showSortOptions.value = true },
                showSleepTimerAlertDialog = { showSleepTimerAlertDialog.value = true }
            )
        }, bottomBar = {
            selectedTrack?.let { SeekbarView(it, audioPlayer, viewModel) }
        }) {
        if (filteredTracks.isEmpty()) {
            if (searchText.isNotEmpty()) {
                ContentUnavailableView(titleSuffix = searchText)
            } else {
                ContentUnavailableView(
                    titleSuffix = null,
                    title = stringResource(R.string.home_content_not_available_text)
                )
            }
        } else {
            Box {
                LazyColumn(state = listState, modifier = Modifier.padding(it)) {
                    itemsIndexed(filteredTracks, key = { _, track -> track.id }) { index, track ->
                        ListItemText(
                            track.title,
                            color = track.color(selectedTrack?.id),
                            action = {
                                coroutineScope.launch(Dispatchers.Main) {
                                    listState.animateScrollToItem(index)
                                }

                                viewModel.playTrack(track)
                            },
                            longPressAction = {
                                selectedTrackForTrackAction.value = track
                                showTrackActionsBottomSheet.value = true
                            }
                        )
                    }
                }
            }

            if (showTrackActionsBottomSheet.value) {
                val bottomSheetAction = arrayListOf(
                    Triple(
                        R.drawable.ic_add_playlist,
                        R.string.home_track_action_add_to_playlist
                    ) {
                        showAddToPlaylistDialog.value = true
                        showTrackActionsBottomSheet.value = false
                    },
                    Triple(
                        R.drawable.ic_save,
                        R.string.home_bottom_sheet_save_track_position_action_title
                    ) {
                        audioPlayer.saveCurrentTrackPlaybackPosition()
                    },
                    Triple(
                        R.drawable.ic_reset,
                        R.string.home_bottom_sheet_reset_track_position_action_title
                    ) {
                        audioPlayer.resetCurrentTrackPlaybackPosition()
                    },
                    Triple(
                        R.drawable.ic_delete,
                        R.string.home_bottom_sheet_delete_track_action_title
                    ) {
                        showDeleteTrackDialog.value = true
                    },
                )


                MoreActionsBottomSheet(
                    title = selectedTrackForTrackAction.value?.title,
                    actions = bottomSheetAction,
                    dismiss = { showTrackActionsBottomSheet.value = false }
                )
            }

            if (showAddToPlaylistDialog.value) {
                SimpleAlertDialog(
                    titleId = R.string.add_to_playlist_alert_dialog_title,
                    description = stringResource(id = R.string.add_to_playlist_alert_dialog_description),
                    dismiss = {
                        showTrackActionsBottomSheet.value = false
                        showAddToPlaylistDialog.value = false
                    },
                    onComplete = {}, content = {
                        LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(it)) {
                            val sortedPlaylists = playlists.toList().sortedBy { (key, _) -> key }
                            itemsIndexed(sortedPlaylists) { _, entry ->
                                val (playlistTitle, _) = entry

                                Text(
                                    text = playlistTitle,
                                    style = MaterialTheme.typography.headlineSmall,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .basicMarquee()
                                        .padding(all = 8.dp)
                                        .clickable {
                                            selectedTrackForTrackAction.value?.let {
                                                playlistsViewModel.addTrackToPlaylist(it.titleRepresentation(), playlistTitle)
                                                showTrackActionsBottomSheet.value = false
                                                showAddToPlaylistDialog.value = false
                                                context.showToast(
                                                    context.getString(
                                                        R.string.playlist_screen_add,
                                                        it.titleRepresentation(),
                                                        playlistTitle
                                                    )
                                                )
                                            }
                                        },
                                    color = AppColors.textColor,
                                )

                                HorizontalDivider()
                            }
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

            if (showDeleteTrackDialog.value) {
                SimpleAlertDialog(
                    titleId = R.string.delete_alert_dialog_title,
                    description = stringResource(R.string.delete_alert_dialog_description),
                    onComplete = {
                        selectedTrackForTrackAction.value?.let {
                            if (it == selectedTrack) {
                                viewModel.playNextTrack()
                            }

                            FolderAnalyzer.deleteTrack(it)
                            viewModel.deleteTrack(it)
                            ToastManager.showDeleteSuccessMessage()
                        }
                    },
                    dismiss = {
                        showTrackActionsBottomSheet.value = false
                        showDeleteTrackDialog.value = false
                    }
                )
            }

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
