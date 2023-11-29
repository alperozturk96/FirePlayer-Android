package com.coolnexttech.fireplayer.view

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.VSpacing16
import com.coolnexttech.fireplayer.extensions.VSpacing8
import com.coolnexttech.fireplayer.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.extensions.isTrackAvailable
import com.coolnexttech.fireplayer.extensions.startPlayerServiceWithDelay
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.components.BodyMediumText
import com.coolnexttech.fireplayer.ui.components.DialogButton
import com.coolnexttech.fireplayer.ui.components.Drawable
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel
import com.coolnexttech.fireplayer.viewModel.HomeViewModel
import com.coolnexttech.fireplayer.viewModel.ViewModelProvider

class HomeView(
    private val selectedPlaylistTitle: String?,
    private val viewModel: HomeViewModel,
    private val audioPlayerViewModel: AudioPlayerViewModel
) : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val context = LocalContext.current
        val folderAnalyzer = FolderAnalyzer(context)
        val filteredTracks by viewModel.filteredTracks.collectAsState()
        val searchText by viewModel.searchText.collectAsState()
        val filterOption by viewModel.filterOption.collectAsState()
        val showSortOptions = remember { mutableStateOf(false) }
        val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            viewModel.initTrackList(folderAnalyzer, selectedPlaylistTitle)
        }

        LaunchedEffect(searchText) {
            viewModel.search(searchText)
        }

        LaunchedEffect(filterOption) {
            viewModel.search(searchText)
        }

        LaunchedEffect(selectedTrackIndex) {
            selectedTrackIndex?.let {
                if (filteredTracks.isTrackAvailable()) {
                    audioPlayerViewModel.play(filteredTracks[it].path)
                    viewModel.updatePrevTracks()
                    context.startPlayerServiceWithDelay()
                    listState.animateScrollToItem(it)
                } else {
                    audioPlayerViewModel.stop()
                }
            }
        }

        Scaffold(
            topBar = {
                TopBar(
                    context,
                    searchText,
                    viewModel,
                    showSortOptions = {
                        showSortOptions.value = true
                    })
            },
            bottomBar = {
                selectedTrackIndex?.let {
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
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .combinedClickable(
                                    onClick = { viewModel.selectTrack(index) },
                                    onLongClick = {
                                        navigator?.push(
                                            PlaylistsView(
                                                track.title,
                                                PlaylistViewMode.Add,
                                                ViewModelProvider.playlistViewModel
                                            )
                                        )
                                    },
                                ),
                            color = if (selectedTrackIndex == index) AppColors.highlight else AppColors.textColor
                        )

                        Divider()
                    }
                }

                if (showSortOptions.value) {
                    SortOptionsAlertDialog(
                        dismiss = { showSortOptions.value = false },
                        sortByTitle = { sortOption ->
                            viewModel.sort(sortOption)
                            showSortOptions.value = false
                        })
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    context: Context,
    searchText: String,
    viewModel: HomeViewModel,
    showSortOptions: () -> Unit,
) {
    val navigator = LocalNavigator.current
    val filterOption by viewModel.filterOption.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val folderAnalyzer = FolderAnalyzer(context)

    TopAppBar(
        colors = getTopAppBarColor(),
        title = {
            BasicTextField(
                value = searchText,
                onValueChange = {
                    viewModel.updateSearchText(it)
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
                navigator?.push(
                    PlaylistsView(
                        null,
                        PlaylistViewMode.Select,
                        ViewModelProvider.playlistViewModel
                    )
                )
            }

            ActionIconButton(filterOption.filterOptionIconId()) {
                viewModel.changeFilterOption()
            }

            ActionIconButton(playMode.getIconId()) {
                viewModel.changePlayMode()
            }

            ActionIconButton(R.drawable.ic_sort) {
                showSortOptions()
            }

            ActionIconButton(R.drawable.ic_reset) {
                viewModel.initTrackList(folderAnalyzer, null)
            }
        }
    )
}

@Composable
private fun SortOptionsAlertDialog(
    dismiss: () -> Unit,
    sortByTitle: (SortOptions) -> Unit,
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

            Drawable(R.drawable.im_app_icon)

            VSpacing16()

            HeadlineSmallText(R.string.home_sort_dialog_title)

            VSpacing8()

            DialogButton(R.string.home_sort_dialog_sort_by_title_a_z_title) {
                sortByTitle(SortOptions.AtoZ)
            }

            DialogButton(R.string.home_sort_dialog_sort_by_title_z_a_title) {
                sortByTitle(SortOptions.ZtoA)
            }

            DialogButton(R.string.common_cancel) {
                dismiss()
            }

            VSpacing16()
        }
    }
}
