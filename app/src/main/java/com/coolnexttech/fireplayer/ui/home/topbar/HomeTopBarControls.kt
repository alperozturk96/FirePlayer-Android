package com.coolnexttech.fireplayer.ui.home.topbar

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.utils.extensions.showToast
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate

@Composable
fun HomeTopBarControls(
    context: Context,
    viewModel: HomeViewModel,
    navController: NavController<Destination>,
    searchText: String,
    filterOption: FilterOptions,
    playMode: PlayMode,
    addTrackToPlaylist: () -> Unit,
    showSortOptions: () -> Unit,
) {
    Row {
        ActionIconButton(R.drawable.ic_playlists) {
            navController.navigate(Destination.Playlists(PlaylistViewMode.Select))
        }

        ActionIconButton(filterOption.filterOptionIconId()) {
            viewModel.changeFilterOption(searchText)
            context.showToast(
                filterOption.selectNextFilterOption().searchTitleId()
            )
        }

        ActionIconButton(playMode.getIconId()) {
            viewModel.changePlayMode()
        }

        ActionIconButton(R.drawable.ic_sort) {
            showSortOptions()
        }

        ActionIconButton(R.drawable.ic_add_playlist) {
            addTrackToPlaylist()
            context.showToast(
                R.string.home_add_track_to_playlist_hint
            )
        }
    }
}