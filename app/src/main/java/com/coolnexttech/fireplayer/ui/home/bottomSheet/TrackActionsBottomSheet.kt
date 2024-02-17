package com.coolnexttech.fireplayer.ui.home.bottomSheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.player.AudioPlayer
import com.coolnexttech.fireplayer.ui.components.bottomSheet.MoreActionsBottomSheet

@Composable
fun TrackActionsBottomSheet(
    audioPlayer: AudioPlayer,
    selectedTrackForTrackAction: MutableState<Track?>,
    showDeleteTrackAlertDialog: MutableState<Boolean>,
    showTrackActionsBottomSheet: MutableState<Boolean>,
    showAddTrackToPlaylistDialog: MutableState<Boolean>
) {
    if (showTrackActionsBottomSheet.value) {
        val bottomSheetAction = arrayListOf(
            Triple(
                R.drawable.ic_add_playlist,
                R.string.home_track_action_add_to_playlist
            ) {
                showAddTrackToPlaylistDialog.value = true
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
                showDeleteTrackAlertDialog.value = true
            },
        )


        MoreActionsBottomSheet(
            title = selectedTrackForTrackAction.value?.title,
            actions = bottomSheetAction,
            dismiss = { showTrackActionsBottomSheet.value = false }
        )
    }
}
