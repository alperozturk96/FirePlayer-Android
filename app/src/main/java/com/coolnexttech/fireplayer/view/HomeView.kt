package com.coolnexttech.fireplayer.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.VSpacing16
import com.coolnexttech.fireplayer.extensions.VSpacing8
import com.coolnexttech.fireplayer.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.ActionButton
import com.coolnexttech.fireplayer.ui.components.BodyMediumText
import com.coolnexttech.fireplayer.ui.components.DialogButton
import com.coolnexttech.fireplayer.ui.components.Drawable
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
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
    val filterOption = remember { mutableStateOf(FilterOptions.Title) }
    val searchText = remember { mutableStateOf("") }
    val playMode = remember { mutableStateOf(PlayMode.Shuffle) }
    val showSortOptions = remember { mutableStateOf(false) }
    val selectedTrackIndex = remember { mutableIntStateOf(-1) }
    val prevTrackIndexesStack = remember { mutableStateListOf<Int>() }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.initTrackList(folderAnalyzer)
    }

    LaunchedEffect(selectedTrackIndex.intValue) {
        if (selectedTrackIndex.intValue != -1) {
            audioPlayerViewModel.play(context, filteredTracks[selectedTrackIndex.intValue].path)
            listState.animateScrollToItem(selectedTrackIndex.intValue)
        }
    }

    val pickFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            val tracks = folderAnalyzer.getTracksFromSelectedPath(uri)
            viewModel.updateTrackList(tracks)
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
                    pickFolderLauncher.launch(null)
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
            LazyColumn(state = listState, modifier = Modifier.padding(it)) {
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
                    dismiss = { showSortOptions.value = false },
                    sortByTitle = { sortOption ->
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

    TopAppBar(
        colors = getTopAppBarColor(),
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
            ActionButton(R.drawable.ic_playlists) {
                navController.navigate(Destinations.Playlists)
            }

            ActionButton(filterOption.filterOptionIconId()) {
                changeFilterOption(filterOption.selectNextFilterOption())
            }

            ActionButton(playMode.getIconId()) {
                changePlayMode(playMode.selectNextPlayMode())
            }

            ActionButton(R.drawable.ic_sort) {
                showSortOptions()
            }

            ActionButton(R.drawable.ic_folder) {
                selectFolder()
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
    if (filteredTracks.isEmpty()) {
        return
    }

    val nextIndex = when (playMode) {
        PlayMode.Shuffle -> filteredTracks.indices.random()
        PlayMode.Sequential -> (selectedTrackIndex + 1).takeIf { it < filteredTracks.size } ?: 0
    }
    updateSelectedTrackIndex(nextIndex)
}