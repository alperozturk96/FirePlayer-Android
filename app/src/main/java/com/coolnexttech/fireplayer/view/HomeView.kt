package com.coolnexttech.fireplayer.view

import android.app.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.ui.components.ActionButton
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.viewModel.AudioPlayerViewModel
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import com.coolnexttech.fireplayer.viewModel.HomeViewModel

@Composable
fun HomeView(
    navController: NavController,
    viewModel: HomeViewModel,
    audioPlayerViewModel: AudioPlayerViewModel
) {
    val context = LocalContext.current
    val folderAnalyzer = FolderAnalyzer(context)
    val trackList by viewModel.trackList.collectAsState()
    val filterOption = remember { mutableStateOf(FilterOptions.title) }
    val playMode = remember { mutableStateOf(PlayMode.shuffle) }
    val showSortOptions = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initTrackList(folderAnalyzer)
    }

    Scaffold(
        topBar = {
            TopBar(
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
                selectFolder = {})
        },
        bottomBar = { BottomBar(audioPlayerViewModel) }) {
        if (trackList.isEmpty()) {
            ContentUnavailable()
        } else {
            LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(it)) {
                items(trackList) { track ->
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .clickable {
                                audioPlayerViewModel.play(context, track.path)
                            },
                        color = AppColors.textColor
                    )
                }
            }

            if (showSortOptions.value) {
                SortOptionsAlertDialog(
                    dismiss = { showSortOptions.value = !showSortOptions.value },
                    sortByTitle = { isAtoZ ->

                    })
            }

        }
    }
}

// FIXME not looking good
@Composable
private fun ContentUnavailable() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Please add tracks into your music folder",
            style = MaterialTheme.typography.headlineLarge
        )
        // Optionally include an icon or image
        Icon(
            painter = painterResource(id = R.drawable.ic_folder),
            contentDescription = "No content available",
            modifier = Modifier.size(128.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    filterOption: FilterOptions,
    playMode: PlayMode,
    changeFilterOption: (FilterOptions) -> Unit,
    changePlayMode: (PlayMode) -> Unit,
    showSortOptions: () -> Unit,
    selectFolder: () -> Unit
) {
    val filterOptionIcon = when (filterOption) {
        FilterOptions.title -> R.drawable.ic_title
        FilterOptions.album -> R.drawable.ic_album
        FilterOptions.artist -> R.drawable.ic_artist
    }

    val playModeIcon = when (playMode) {
        PlayMode.shuffle -> R.drawable.ic_shuffle
        PlayMode.sequential -> R.drawable.ic_sequential
    }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColors.background,
            scrolledContainerColor = AppColors.background,
            navigationIconContentColor = AppColors.unHighlight,
            titleContentColor = AppColors.unHighlight,
            actionIconContentColor = AppColors.unHighlight
        ),
        title = { /* Add title if needed */ },
        actions = {
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
                .height(400.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(AppColors.alternateBackground),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Button({ sortByTitle(true) }) {
                Text(text = "Sort by Title A-Z", color = AppColors.textColor)
            }
            Button({ sortByTitle(false) }) {
                Text(text = "Sort by Title Z-A", color = AppColors.textColor)
            }
            Button({ dismiss() }) {
                Text(text = "Cancel", color = AppColors.textColor)
            }
        }
    }
}

@Composable
private fun BottomBar(audioPlayerViewModel: AudioPlayerViewModel) {
    SeekbarView(audioPlayerViewModel, selectPreviousTrack = { /*TODO*/ }) {

    }
}