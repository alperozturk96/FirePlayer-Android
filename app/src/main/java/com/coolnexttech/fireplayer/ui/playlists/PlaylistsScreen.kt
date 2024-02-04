package com.coolnexttech.fireplayer.ui.playlists

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.bottomSheet.MoreActionsBottomSheet
import com.coolnexttech.fireplayer.ui.components.dialog.AddPlaylistAlertDialog
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.ui.playlists.topbar.PlaylistsTopBar
import com.coolnexttech.fireplayer.utils.VMProvider
import com.coolnexttech.fireplayer.utils.extensions.showToast
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
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

    BackHandler {
        VMProvider.homeViewModel.initTrackList(null)
        navController.pop()
    }

    Scaffold(topBar = {
        PlaylistsTopBar(viewModel) {
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
                    },
                    longPressAction = {
                        selectedPlaylistTitle = playlistTitle
                        showBottomSheet = true
                    }
                )
            }
        }
    }

    if (showBottomSheet) {
        val bottomSheetAction = listOf(
            Triple(
                R.drawable.ic_delete,
                R.string.playlist_bottom_sheet_delete_action_title
            ) {
                viewModel.removePlaylist(selectedPlaylistTitle)
                showBottomSheet = false
            }
        )

        MoreActionsBottomSheet(
            actions = bottomSheetAction,
            dismiss = { showBottomSheet = false }
        )
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
        viewModel.addTrackToPlaylist(mode.trackTitleRepresentation, playlistTitle)

        context.showToast(
            context.getString(
                R.string.playlist_screen_add,
                mode.trackTitle,
                playlistTitle
            )
        )

        navController.pop()
    } else {
        VMProvider.homeViewModel.initTrackList(playlistTitle)
        navController.navigate(Destination.Home)
    }
}
