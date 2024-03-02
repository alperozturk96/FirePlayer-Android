package com.coolnexttech.fireplayer.ui.components.dialog

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun AddPlaylistAlertDialog(
    viewModel: PlaylistsViewModel,
    dismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { dismiss() },
        title = {
            Text(text = stringResource(id = R.string.playlist_screen_add_playlist_dialog_title))
        },
        text = {
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = AppColors.secondaryContainer,
                    unfocusedContainerColor = AppColors.secondaryContainer,
                ),
                placeholder = {
                    Text(text = stringResource(id = R.string.playlist_screen_add_playlist_placeholder))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                value = title,
                onValueChange = {
                    title = it
                },
                singleLine = true
            )
        },
        confirmButton = {
            FilledTonalButton(onClick = {
                dismiss()
                viewModel.addPlaylist(title)
            }) {
                Text(stringResource(id = R.string.common_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(stringResource(id = R.string.common_cancel))
            }
        }
    )
}
