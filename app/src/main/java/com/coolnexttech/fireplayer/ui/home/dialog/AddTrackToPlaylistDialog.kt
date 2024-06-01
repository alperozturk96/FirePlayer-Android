package com.coolnexttech.fireplayer.ui.home.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.db.PlaylistEntity
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.ui.theme.AppColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddTrackToPlaylistDialog(
    playlists: List<PlaylistEntity>,
    padding: PaddingValues,
    addToPlaylist: (String) -> Unit,
    dismiss: () -> Unit
) {
    SimpleAlertDialog(
        heightFraction = 0.5f,
        titleId = R.string.add_to_playlist_alert_dialog_title,
        description = stringResource(id = R.string.add_to_playlist_alert_dialog_description),
        dismiss = {
            dismiss()
        },
        onComplete = {}, content = {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.padding(padding)
            ) {
                val sortedPlaylists = playlists.toList().sortedBy { (key, _) -> key }
                itemsIndexed(sortedPlaylists) { _, entry ->

                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .basicMarquee()
                            .padding(all = 8.dp)
                            .clickable {
                                addToPlaylist(entry.title)
                                dismiss()
                            },
                        color = AppColors.textColor,
                    )

                    HorizontalDivider(color = AppColors.textColor)
                }
            }
        }
    )
}
