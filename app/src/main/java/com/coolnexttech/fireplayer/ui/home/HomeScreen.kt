package com.coolnexttech.fireplayer.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.service.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.dialog.SortOptionsAlertDialog
import com.coolnexttech.fireplayer.ui.components.view.ContentUnavailableView
import com.coolnexttech.fireplayer.ui.components.view.SeekbarView
import com.coolnexttech.fireplayer.ui.home.topbar.HomeTopBar
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.ui.theme.AppColors
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController<Destination>,
    viewModel: HomeViewModel,
    audioPlayer: AudioPlayer
) {
    val context = LocalContext.current
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val filterOption by viewModel.filterOption.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val showSortOptions = remember { mutableStateOf(false) }
    val addTrackToPlaylist = remember { mutableStateOf(false) }

    val showAlphabeticalScroller = remember { mutableStateOf(false) }
    val alphabeticalScrollerIconId = if (showAlphabeticalScroller.value) {
        R.drawable.ic_arrow_up
    } else {
        R.drawable.ic_arrow_down
    }

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
                navController,
                playMode,
                alphabeticalScrollerIconId,
                filterOption,
                searchText,
                viewModel,
                showAlphabeticalScroller,
                characterList,
                coroutineScope,
                listState,
                addTrackToPlaylist = { addTrackToPlaylist.value = true },
                showSortOptions = { showSortOptions.value = true })
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
                        val textColor =
                            if (selectedTrack?.id == track.id) AppColors.highlight else AppColors.textColor

                        ListItemText(
                            track.titleRepresentation(),
                            color = textColor,
                            action = {
                                listItemAction(
                                    addTrackToPlaylist,
                                    navController,
                                    track,
                                    coroutineScope,
                                    listState,
                                    index,
                                    viewModel
                                )
                            })
                    }
                }
            }

            if (showSortOptions.value) {
                SortOptionsAlertDialog(
                    dismiss = { showSortOptions.value = false },
                    sort = { sortOption ->
                        viewModel.sort(sortOption)
                        showSortOptions.value = false
                    })
            }
        }
    }
}

private fun listItemAction(
    addTrackToPlaylist: MutableState<Boolean>,
    navController: NavController<Destination>,
    track: Track,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    index: Int,
    viewModel: HomeViewModel
) {
    if (addTrackToPlaylist.value) {
        navController.navigate(
            Destination.Playlists(
                PlaylistViewMode.Add(track.id, track.title)
            )
        )

        addTrackToPlaylist.value = false
    } else {
        coroutineScope.launch(Dispatchers.Main) {
            listState.animateScrollToItem(index)
        }

        viewModel.playTrack(track)
    }
}
