package com.coolnexttech.fireplayer.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.components.BodyMediumText
import com.coolnexttech.fireplayer.ui.components.ContentUnavailableView
import com.coolnexttech.fireplayer.ui.components.DialogButton
import com.coolnexttech.fireplayer.ui.components.Drawable
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.MoreActionsBottomSheet
import com.coolnexttech.fireplayer.ui.components.SeekbarView
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.VSpacing16
import com.coolnexttech.fireplayer.utils.extensions.VSpacing8
import com.coolnexttech.fireplayer.utils.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.utils.extensions.queryById
import com.coolnexttech.fireplayer.utils.extensions.startPlayerServiceWithDelay
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate

@Composable
fun HomeScreen(
    navController: NavController<Destination>,
    viewModel: HomeViewModel,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val context = LocalContext.current
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val showSortOptions = remember { mutableStateOf(false) }
    val selectedTrackIdForListScroll: MutableState<Int?> = remember { mutableStateOf(null) }
    val selectedTrackIdForAddPlaylist: MutableState<Long?> = remember { mutableStateOf(null) }
    val selectedTrackId by viewModel.selectedTrackId.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(selectedTrackId) {
        if (selectedTrackId == null) {
            return@LaunchedEffect
        }

        val track = filteredTracks.queryById(selectedTrackId) ?: return@LaunchedEffect
        audioPlayerViewModel.play(track.path)
        viewModel.updatePrevTracks()
        context.startPlayerServiceWithDelay()

        selectedTrackIdForListScroll.value?.let {
            listState.animateScrollToItem(it)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                searchText,
                viewModel,
                showSortOptions = {
                    showSortOptions.value = true
                })
        },
        bottomBar = {
            selectedTrackId?.let {
                SeekbarView(audioPlayerViewModel, viewModel)
            }
        }) {
        if (filteredTracks.isEmpty()) {
            if (searchText.isNotEmpty()) {
                ContentUnavailableView(titleSuffix = searchText)
            } else {
                ContentUnavailableView(
                    titleSuffix = null,
                    title = stringResource(R.string.home_content_not_available_text)
                )
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.padding(it)) {
                itemsIndexed(filteredTracks) { index, track ->
                    val textColor =
                        if (selectedTrackId == track.id) AppColors.highlight else AppColors.textColor

                    ListItemText(track.title, textColor, action = {
                        selectedTrackIdForListScroll.value = index
                        viewModel.selectTrack(track.id)
                    }) {
                        selectedTrackIdForAddPlaylist.value = track.id
                    }
                }
            }

            if (selectedTrackIdForAddPlaylist.value != null) {
                MoreActionsBottomSheet(
                    R.string.home_bottom_sheet_add_to_playlist_action_title,
                    dismiss = { selectedTrackIdForAddPlaylist.value = null }) {
                    navController.navigate(
                        Destination.Playlists(
                            PlaylistViewMode.Add(selectedTrackIdForAddPlaylist.value!!)
                        )
                    )
                    selectedTrackIdForAddPlaylist.value = null
                }
            }

            if (showSortOptions.value) {
                SortOptionsAlertDialog(
                    dismiss = { showSortOptions.value = false },
                    sort = { sortOption ->
                        viewModel.sort(sortOption)
                        showSortOptions.value = false
                    })
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController<Destination>,
    searchText: String,
    viewModel: HomeViewModel,
    showSortOptions: () -> Unit,
) {
    val filterOption by viewModel.filterOption.collectAsState()
    val playMode by viewModel.playMode.collectAsState()

    TopAppBar(
        colors = getTopAppBarColor(),
        title = {
            BasicTextField(
                value = searchText,
                onValueChange = {
                    viewModel.search(it)
                },
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (searchText.isEmpty()) {
                        BodyMediumText(id = filterOption.searchTitleId())
                    }
                    innerTextField()
                },
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Min)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(AppColors.unhighlight),
                cursorBrush = SolidColor(AppColors.unhighlight)
            )
        },
        actions = {
            ActionIconButton(R.drawable.ic_playlists) {
                navController.navigate(Destination.Playlists(PlaylistViewMode.Select))
            }

            ActionIconButton(filterOption.filterOptionIconId()) {
                viewModel.changeFilterOption(searchText)
            }

            ActionIconButton(playMode.getIconId()) {
                viewModel.changePlayMode()
            }

            ActionIconButton(R.drawable.ic_sort) {
                showSortOptions()
            }

            ActionIconButton(R.drawable.ic_reset) {
                viewModel.initTrackList(null)
            }
        }
    )
}

@Composable
private fun SortOptionsAlertDialog(
    dismiss: () -> Unit,
    sort: (SortOptions) -> Unit,
) {
    Dialog({ dismiss() }) {
        Column(
            modifier = Modifier
                .width(400.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(AppColors.alternateBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VSpacing16()

            Drawable(R.drawable.ic_fire, tint = AppColors.red)

            VSpacing16()

            HeadlineSmallText(R.string.home_sort_dialog_title)

            VSpacing8()

            DialogButton(R.string.home_sort_dialog_sort_by_title_a_z) {
                sort(SortOptions.AToZ)
            }

            DialogButton(R.string.home_sort_dialog_sort_by_title_z_a) {
                sort(SortOptions.ZToA)
            }

            DialogButton(R.string.home_sort_dialog_sort_by_new_to_old_title) {
                sort(SortOptions.NewToOld)
            }

            DialogButton(R.string.home_sort_dialog_sort_by_old_to_new_title) {
                sort(SortOptions.OldToNew)
            }

            DialogButton(R.string.common_cancel) {
                dismiss()
            }

            VSpacing16()
        }
    }
}
