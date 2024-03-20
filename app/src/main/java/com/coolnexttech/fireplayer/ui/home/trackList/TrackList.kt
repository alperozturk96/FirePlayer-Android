package com.coolnexttech.fireplayer.ui.home.trackList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.view.ContentUnavailableView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrackList(
    listState: LazyListState,
    it: PaddingValues,
    filteredTracks: List<Track>,
    selectedTrack: Track?,
    action: (Pair<Int, Track>) -> Unit,
    longPressAction: (Track) -> Unit
) {
    Box {
        LazyColumn(state = listState, modifier = Modifier.padding(it)) {
            itemsIndexed(filteredTracks, key = { _, track -> track.id }) { index, track ->
                ListItemText(
                    track.title,
                    color = track.color(selectedTrack?.id),
                    action = {
                        action(Pair(index, track))
                    },
                    longPressAction = {
                        longPressAction(track)
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyTrackList(searchText: String) {
    if (searchText.isNotEmpty()) {
        ContentUnavailableView(titleSuffix = searchText)
    } else {
        ContentUnavailableView(
            titleSuffix = null,
            title = stringResource(R.string.home_content_not_available_text)
        )
    }
}
