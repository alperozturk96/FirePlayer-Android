package com.coolnexttech.fireplayer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.ActionButton
import com.coolnexttech.fireplayer.ui.navigation.Destinations
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel
import com.coolnexttech.fireplayer.viewModel.HomeViewModel

@Composable
fun HomeView(
    navController: NavController,
    viewModel: HomeViewModel,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val context = LocalContext.current
    val folderAnalyzer = FolderAnalyzer(context)
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val filterOption = remember { mutableStateOf(FilterOptions.title) }
    val searchText = remember { mutableStateOf("") }
    val playMode = remember { mutableStateOf(PlayMode.shuffle) }
    val showSortOptions = remember { mutableStateOf(false) }
    val selectedTrackIndex = remember { mutableIntStateOf(-1) }
    val prevTrackIndexesStack = remember { mutableStateListOf<Int>() }


    LaunchedEffect(Unit) {
        viewModel.initTrackList(folderAnalyzer)
    }

    LaunchedEffect(selectedTrackIndex.intValue) {
        if (selectedTrackIndex.intValue != -1) {
            audioPlayerViewModel.play(context, filteredTracks[selectedTrackIndex.intValue].path)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                filterOption.value,
                playMode.value,
                changeFilterOption = {
                    filterOption.value = it
                },
                changePlayMode = {
                    playMode.value = it
                },
                showSortOptions = {
                    showSortOptions.value = true
                },
                selectFolder = {

                },
                onSearchQueryChanged = {
                    searchText.value = it
                    viewModel.search(it, filterOption.value)
                })
        },
        bottomBar = {
            SeekbarView(audioPlayerViewModel, {
                selectPreviousTrack(prevTrackIndexesStack) {
                    selectedTrackIndex.intValue = it
                }
            }) {
                selectNextTrack(filteredTracks, playMode.value, selectedTrackIndex.intValue) {
                    selectedTrackIndex.intValue = it
                    prevTrackIndexesStack.add(it)
                }
            }
        }) {
        if (filteredTracks.isEmpty()) {
            ContentUnavailableView(titleSuffix = searchText.value)
        } else {
            LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(it)) {
                itemsIndexed(filteredTracks) { index, track ->
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .clickable {
                                selectedTrackIndex.intValue = index
                                prevTrackIndexesStack.add(index)
                            },
                        color = if (selectedTrackIndex.intValue == index) AppColors.highlight else AppColors.textColor
                    )

                    Divider()
                }
            }

            if (showSortOptions.value) {
                SortOptionsAlertDialog(
                    dismiss = { showSortOptions.value = !showSortOptions.value },
                    sortByTitle = { isAtoZ ->
                        viewModel.sort(isAtoZ)
                    })
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController,
    filterOption: FilterOptions,
    playMode: PlayMode,
    changeFilterOption: (FilterOptions) -> Unit,
    changePlayMode: (PlayMode) -> Unit,
    showSortOptions: () -> Unit,
    selectFolder: () -> Unit,
    onSearchQueryChanged: (String) -> Unit,
) {
    val searchQuery = remember { mutableStateOf("") }

    val filterOptionIcon = when (filterOption) {
        FilterOptions.title -> R.drawable.ic_title
        FilterOptions.album -> R.drawable.ic_album
        FilterOptions.artist -> R.drawable.ic_artist
    }

    val playModeIcon = when (playMode) {
        PlayMode.shuffle -> R.drawable.ic_shuffle
        PlayMode.sequential -> R.drawable.ic_sequential
    }

    val searchTitleId = when (filterOption) {
        FilterOptions.title -> R.string.home_search_in_titles_text
        FilterOptions.album -> R.string.home_search_in_albums_text
        FilterOptions.artist -> R.string.home_search_in_artists_text
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.background,
            scrolledContainerColor = AppColors.background,
            navigationIconContentColor = AppColors.unhighlight,
            titleContentColor = AppColors.unhighlight,
            actionIconContentColor = AppColors.unhighlight
        ),
        title = {
            BasicTextField(
                value = searchQuery.value,
                onValueChange = {
                    searchQuery.value = it
                    onSearchQueryChanged(it)
                },
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (searchQuery.value.isEmpty()) {
                        Text(
                            text = stringResource(id = searchTitleId),
                            style = MaterialTheme.typography.bodyMedium.copy(AppColors.unhighlight),
                        )
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
            ActionButton(R.drawable.ic_playlists) {
                navController.navigate(Destinations.Playlists)
            }

            ActionButton(filterOptionIcon) {
                val newFilterOption = when (filterOption) {
                    FilterOptions.title -> FilterOptions.artist
                    FilterOptions.artist -> FilterOptions.album
                    FilterOptions.album -> FilterOptions.title
                }
                changeFilterOption(newFilterOption)
            }

            ActionButton(playModeIcon) {
                val newPlayMode = when (playMode) {
                    PlayMode.shuffle -> PlayMode.sequential
                    PlayMode.sequential -> PlayMode.shuffle
                }
                changePlayMode(newPlayMode)
            }

            ActionButton(R.drawable.ic_sort) {
                showSortOptions()
            }

            // TODO implement for sdcard
            ActionButton(R.drawable.ic_folder) {
                selectFolder()
            }
        }
    )
}

@Composable
private fun SortOptionsAlertDialog(
    dismiss: () -> Unit,
    sortByTitle: (Boolean) -> Unit,
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
            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.im_app_icon),
                modifier = Modifier.size(60.dp),
                contentDescription = "AppIcon"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.home_sort_dialog_title),
                color = AppColors.textColor,
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button({ sortByTitle(true) }) {
                Text(
                    text = stringResource(id = R.string.home_sort_dialog_sort_by_title_a_z_title),
                    color = AppColors.textColor
                )
            }
            Button({ sortByTitle(false) }) {
                Text(
                    text = stringResource(id = R.string.home_sort_dialog_sort_by_title_z_a_title),
                    color = AppColors.textColor
                )
            }
            Button({ dismiss() }) {
                Text(
                    text = stringResource(id = R.string.common_cancel),
                    color = AppColors.textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun selectPreviousTrack(
    prevTrackIndexesStack: MutableList<Int>,
    updateSelectedTrackIndex: (Int) -> Unit
) {
    if (prevTrackIndexesStack.size > 1) {
        prevTrackIndexesStack.removeLast()
        prevTrackIndexesStack.lastOrNull()?.let { prevIndex ->
            updateSelectedTrackIndex(prevIndex)
        }
    }
}

private fun selectNextTrack(
    filteredTracks: List<Track>,
    playMode: PlayMode,
    selectedTrackIndex: Int,
    updateSelectedTrackIndex: (Int) -> Unit
) {
    val nextIndex = when (playMode) {
        PlayMode.shuffle -> filteredTracks.indices.random()
        PlayMode.sequential -> (selectedTrackIndex + 1).takeIf { it < filteredTracks.size } ?: 0
    }
    updateSelectedTrackIndex(nextIndex)
}