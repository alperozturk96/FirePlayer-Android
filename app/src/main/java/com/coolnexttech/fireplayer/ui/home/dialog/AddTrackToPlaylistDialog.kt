package com.coolnexttech.fireplayer.ui.home.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.showToast

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddTrackToPlaylistDialog(
    showAddTrackToPlaylistDialog: MutableState<Boolean>,
    showTrackActionsBottomSheet: MutableState<Boolean>,
    selectedTrackForTrackAction: MutableState<Track?>,
    playlistsViewModel: PlaylistsViewModel,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val playlists by playlistsViewModel.playlists.collectAsState()

    if (showAddTrackToPlaylistDialog.value) {
        SimpleAlertDialog(
            heightFraction = 0.5f,
            titleId = R.string.add_to_playlist_alert_dialog_title,
            description = stringResource(id = R.string.add_to_playlist_alert_dialog_description),
            dismiss = {
                showTrackActionsBottomSheet.value = false
                showAddTrackToPlaylistDialog.value = false
            },
            onComplete = {}, content = {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.padding(padding)
                ) {
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
                                        playlistsViewModel.addTrackToPlaylist(
                                            it.titleRepresentation(),
                                            playlistTitle
                                        )
                                        showTrackActionsBottomSheet.value = false
                                        showAddTrackToPlaylistDialog.value = false

                                        context.run {
                                            showToast(
                                                getString(
                                                    R.string.playlist_screen_add,
                                                    it.titleRepresentation(),
                                                    playlistTitle
                                                )
                                            )
                                        }
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
}
