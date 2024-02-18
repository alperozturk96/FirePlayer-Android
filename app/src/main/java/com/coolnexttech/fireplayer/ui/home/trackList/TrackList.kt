package com.coolnexttech.fireplayer.ui.home.trackList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.view.ContentUnavailableView
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TrackList(
    listState: LazyListState,
    it: PaddingValues,
    filteredTracks: List<Track>,
    selectedTrack: Track?,
    coroutineScope: CoroutineScope,
    viewModel: HomeViewModel,
    selectedTrackForTrackAction: MutableState<Track?>,
    showTrackActionsBottomSheet: MutableState<Boolean>
) {
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
}

@Composable
fun EmptyTrackList(viewModel: HomeViewModel) {
    val searchText by viewModel.searchText.collectAsState()

    if (searchText.isNotEmpty()) {
        ContentUnavailableView(titleSuffix = searchText)
    } else {
        ContentUnavailableView(
            titleSuffix = null,
            title = stringResource(R.string.home_content_not_available_text)
        )
    }
}
