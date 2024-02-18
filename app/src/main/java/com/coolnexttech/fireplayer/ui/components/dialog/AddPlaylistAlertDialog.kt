package com.coolnexttech.fireplayer.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
        containerColor = AppColors.alternateBackground,
        onDismissRequest = { dismiss() },
        title = {
            Text(text = stringResource(id = R.string.playlist_screen_add_playlist_dialog_title))
        },
        text = {
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedTextColor = AppColors.textColor,
                    focusedContainerColor = AppColors.alternateBackground,
                    unfocusedContainerColor = AppColors.alternateBackground,
                    focusedIndicatorColor = AppColors.textColor,
                    unfocusedIndicatorColor = AppColors.textColor,
                ),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.playlist_screen_add_playlist_placeholder),
                        color = AppColors.textColor,
                    )
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
            TextButton(onClick = {
                dismiss()
                viewModel.addPlaylist(title)
            }) {
                Text(
                    stringResource(id = R.string.common_ok),
                    color = AppColors.textColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { dismiss() }) {
                Text(
                    stringResource(id = R.string.common_cancel),
                    color = AppColors.textColor
                )
            }
        }
    )
}
