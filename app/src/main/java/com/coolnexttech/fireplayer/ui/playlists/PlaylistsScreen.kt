package com.coolnexttech.fireplayer.ui.playlists

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.components.HeadlineMediumText
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.MoreActionsBottomSheet
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.utils.extensions.showToast
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop

@Composable
fun PlaylistsScreen(
    navController: NavController<Destination>,
    mode: PlaylistViewMode,
    viewModel: PlaylistsViewModel
) {
    val context = LocalContext.current
    val playlists by viewModel.playlists.collectAsState()
    val showAddPlaylist = remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedPlaylistTitle by remember { mutableStateOf("") }

    Scaffold(topBar = {
        TopBar {
            showAddPlaylist.value = true
        }
    }) {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(it)) {
            val sortedPlaylists = playlists.toList().sortedBy { (key, _) -> key }
            itemsIndexed(sortedPlaylists) { _, entry ->
                val (playlistTitle, _) = entry

                ListItemText(
                    playlistTitle,
                    action = {
                        playlistAction(
                            context,
                            mode,
                            viewModel,
                            navController,
                            playlistTitle
                        )
                    }) {
                    selectedPlaylistTitle = playlistTitle
                    showBottomSheet = true
                }
            }
        }
    }

    if (showBottomSheet) {
        MoreActionsBottomSheet(
            R.string.playlist_bottom_sheet_delete_action_title,
            dismiss = { showBottomSheet = false }) {
            viewModel.removePlaylist(selectedPlaylistTitle)
            showBottomSheet = false
        }
    }

    if (showAddPlaylist.value) {
        AddPlaylistAlertDialog(viewModel) {
            showAddPlaylist.value = false
        }
    }
}

private fun playlistAction(
    context: Context,
    mode: PlaylistViewMode,
    viewModel: PlaylistsViewModel,
    navController: NavController<Destination>,
    playlistTitle: String
) {
    if (mode is PlaylistViewMode.Add) {
        viewModel.addTrackToPlaylist(mode.trackId, playlistTitle)
        context.showToast(context.getString(R.string.playlist_screen_add, mode.trackTitle, playlistTitle))
    } else {
        VMProvider.homeViewModel.initTrackList(playlistTitle)
        context.showToast(context.getString(R.string.playlist_screen_selected_playlist, playlistTitle))
    }

    navController.pop()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(showAddPlaylist: () -> Unit) {
    TopAppBar(
        colors = getTopAppBarColor(),
        title = {
            HeadlineMediumText(text = stringResource(id = R.string.playlists_title))
        },
        actions = {
            ActionIconButton(R.drawable.ic_add_playlist) {
                showAddPlaylist()
            }
        }
    )
}

@Composable
private fun AddPlaylistAlertDialog(
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
                    focusedTextColor = AppColors.unhighlight,
                    focusedContainerColor = AppColors.alternateBackground,
                    unfocusedContainerColor = AppColors.alternateBackground,
                    focusedIndicatorColor = AppColors.unhighlight,
                    unfocusedIndicatorColor = AppColors.unhighlight,
                ),
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text(text = stringResource(id = R.string.playlist_screen_add_playlist_placeholder)) },
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