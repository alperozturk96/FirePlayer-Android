package com.coolnexttech.fireplayer.ui.home.bottomSheet

import androidx.compose.runtime.Composable
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.bottomSheet.MoreActionsBottomSheet

@Composable
fun TrackActionsBottomSheet(
    audioPlayer: AudioPlayer,
    trackTitle: String,
    showDeleteTrackAlertDialog: () -> Unit,
    dismiss: () -> Unit,
    showAddTrackToPlaylistDialog: () -> Unit,
) {
    val bottomSheetAction = arrayListOf(
        Triple(
            R.drawable.ic_add_playlist,
            R.string.home_track_action_add_to_playlist
        ) {
            showAddTrackToPlaylistDialog()
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
            showDeleteTrackAlertDialog()
        },
    )

    MoreActionsBottomSheet(
        title = trackTitle,
        actions = bottomSheetAction,
        dismiss = { dismiss() }
    )
}
