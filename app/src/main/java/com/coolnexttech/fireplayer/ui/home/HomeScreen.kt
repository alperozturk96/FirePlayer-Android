package com.coolnexttech.fireplayer.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.dialog.SortOptionsAlertDialog
import com.coolnexttech.fireplayer.ui.components.view.ContentUnavailableView
import com.coolnexttech.fireplayer.ui.components.view.SeekbarView
import com.coolnexttech.fireplayer.ui.home.bottomSheet.TrackActionsBottomSheet
import com.coolnexttech.fireplayer.ui.home.dialog.AddTrackToPlaylistDialog
import com.coolnexttech.fireplayer.ui.home.dialog.DeleteTrackAlertDialog
import com.coolnexttech.fireplayer.ui.home.dialog.SleepTimerAlertDialog
import com.coolnexttech.fireplayer.ui.home.topbar.HomeTopBar
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
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
    val filterOption by viewModel.filterOption.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val showSortOptions = remember { mutableStateOf(false) }
    val showSleepTimerAlertDialog = remember { mutableStateOf(false) }
    val selectedTrackForTrackAction = remember { mutableStateOf<Track?>(null) }
    val showAddTrackToPlaylistDialog = remember { mutableStateOf(false) }
    val showTrackActionsBottomSheet = remember { mutableStateOf(false) }
    val showAlphabeticalScroller = remember { mutableStateOf(false) }
    val alphabeticalScrollerIconId = if (showAlphabeticalScroller.value) {
        R.drawable.ic_arrow_up
    } else {
        R.drawable.ic_arrow_down
    }
    val showDeleteTrackAlertDialog = remember { mutableStateOf(false) }
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

            SleepTimerAlertDialog(audioPlayer, showSleepTimerAlertDialog, showTrackActionsBottomSheet)
        }
    }
}
