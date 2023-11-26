package com.coolnexttech.fireplayer.view

import android.util.Log
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
import androidx.navigation.NavController
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.VSpacing16
import com.coolnexttech.fireplayer.extensions.VSpacing8
import com.coolnexttech.fireplayer.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.extensions.startPlayerServiceWithDelay
import com.coolnexttech.fireplayer.model.SortOptions
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
    val searchText by viewModel.searchText.collectAsState()
    val showSortOptions = remember { mutableStateOf(false) }
    val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.initTrackList(folderAnalyzer)
        Log.d("Home","Total Track Count: " + filteredTracks.count())
    }

    LaunchedEffect(selectedTrackIndex) {
        if (selectedTrackIndex != -1) {
            audioPlayerViewModel.play(filteredTracks[selectedTrackIndex].path)
            context.startPlayerServiceWithDelay()
            listState.animateScrollToItem(selectedTrackIndex)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController,
                viewModel,
                showSortOptions = {
                    showSortOptions.value = true
                })
        },
        bottomBar = {
            SeekbarView(audioPlayerViewModel, viewModel)
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
                            .clickable {
                                viewModel.selectTrack(index)
                                context.startPlayerServiceWithDelay()
                            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    navController: NavController,
    viewModel: HomeViewModel,
    showSortOptions: () -> Unit,
) {
    val filterOption by viewModel.filterOption.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
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
            ActionButton(R.drawable.ic_playlists) {
                navController.navigate(Destinations.Playlists)
            }

            ActionButton(filterOption.filterOptionIconId()) {
                viewModel.changeFilterOption()
            }

            ActionButton(playMode.getIconId()) {
                viewModel.changePlayMode()
            }

            ActionButton(R.drawable.ic_sort) {
                showSortOptions()
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
