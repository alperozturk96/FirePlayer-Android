package com.coolnexttech.fireplayer.ui.home.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.dialog.SimpleAlertDialog
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.ToastManager

@Composable
fun DeleteTrackAlertDialog(
    viewModel: HomeViewModel,
    showDeleteTrackAlertDialog: MutableState<Boolean>,
    showTrackActionsBottomSheet: MutableState<Boolean>,
    selectedTrackForTrackAction: MutableState<Track?>,
    selectedTrack: Track?,
) {
    val searchText by viewModel.searchText.collectAsState()

    if (showDeleteTrackAlertDialog.value) {
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
}