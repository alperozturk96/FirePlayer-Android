package com.coolnexttech.fireplayer.ui.playlists.topbar

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.components.HeadlineMediumText
import com.coolnexttech.fireplayer.ui.playlists.PlaylistsViewModel
import com.coolnexttech.fireplayer.utils.UserStorage
import com.coolnexttech.fireplayer.utils.extensions.getTopAppBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsTopBar(viewModel: PlaylistsViewModel, showAddPlaylist: () -> Unit) {
    TopAppBar(
        colors = getTopAppBarColor(),
        title = {
            HeadlineMediumText(text = stringResource(id = R.string.playlists_title))
        },
        actions = {
            ActionIconButton(R.drawable.ic_add_playlist) {
                showAddPlaylist()
            }

            ActionIconButton(R.drawable.ic_import) {
                UserStorage.importPlaylists()
                viewModel.readPlaylists()
            }

            ActionIconButton(R.drawable.ic_export) {
                UserStorage.exportPlaylists()
            }
        }
    )
}
