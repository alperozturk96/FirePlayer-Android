package com.coolnexttech.fireplayer.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.model.PlaylistViewMode
import com.coolnexttech.fireplayer.model.SortOptions
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.ActionIconButton
import com.coolnexttech.fireplayer.ui.components.BodyMediumText
import com.coolnexttech.fireplayer.ui.components.ContentUnavailableView
import com.coolnexttech.fireplayer.ui.components.DialogButton
import com.coolnexttech.fireplayer.ui.components.Drawable
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.components.ListItemText
import com.coolnexttech.fireplayer.ui.components.SeekbarView
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.FolderAnalyzer
import com.coolnexttech.fireplayer.utils.extensions.VSpacing16
import com.coolnexttech.fireplayer.utils.extensions.VSpacing8
import com.coolnexttech.fireplayer.utils.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.utils.extensions.showToast
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController<Destination>,
    viewModel: HomeViewModel,
    audioPlayer: AudioPlayer
) {
    val context = LocalContext.current
    val filteredTracks by viewModel.filteredTracks.collectAsState()
    val filterOption by viewModel.filterOption.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val showSortOptions = remember { mutableStateOf(false) }
    val addTrackToPlaylist = remember { mutableStateOf(false) }

    val showAlphabeticalScroller = remember { mutableStateOf(false) }
    val alphabeticalScrollerIconId = if (showAlphabeticalScroller.value) {
        R.drawable.ic_arrow_up
    } else {
        R.drawable.ic_arrow_down
    }

    val showOptions = remember { mutableStateOf(false) }
    val optionsIconId = if (showOptions.value) {
        R.drawable.ic_arrow_right
    } else {
        R.drawable.ic_arrow_left
    }

    val selectedTrack by viewModel.selectedTrack.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val characterList = remember(filteredTracks) {
        filteredTracks.groupBy { it.title.first().uppercaseChar() }
            .mapValues { (_, tracks) -> filteredTracks.indexOf(tracks.first()) }
    }

    Scaffold(
        topBar = {
            TopBar(
                alphabeticalScrollerIconId,
                filterOption,
                searchText,
                viewModel,
                showAlphabeticalScroller,
                characterList,
                coroutineScope,
                listState
            )
        }, bottomBar = {
            if (selectedTrack != null) {
                SeekbarView(audioPlayer, viewModel)
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
            Box {
                LazyColumn(state = listState, modifier = Modifier.padding(it)) {
                    itemsIndexed(filteredTracks) { index, track ->
                        val textColor =
                            if (selectedTrack?.id == track.id) AppColors.highlight else AppColors.textColor

                        ListItemText(
                            track.title,
                            color = textColor,
                            action = {
                                listItemAction(
                                    addTrackToPlaylist,
                                    navController,
                                    track,
                                    coroutineScope,
                                    listState,
                                    index,
                                    viewModel
                                )
                            })
                    }
                }

                SideControls(
                    context,
                    viewModel,
                    navController,
                    modifier = Modifier
                        .background(AppColors.background)
                        .align(Alignment.CenterEnd),
                    optionsIconId,
                    searchText ,
                    filterOption,
                    playMode,
                    showOptions.value,
                    addTrackToPlaylist = { addTrackToPlaylist.value = true },
                    showSortOptions = { showSortOptions.value = true },
                    showOptions = { showOptions.value = !showOptions.value })
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

@Composable
private fun TopBar(
    alphabeticalScrollerIconId: Int,
    filterOption: FilterOptions,
    searchText: String,
    viewModel: HomeViewModel,
    showAlphabeticalScroller: MutableState<Boolean>,
    characterList: Map<Char, Int>,
    coroutineScope: CoroutineScope,
    listState: LazyListState
) {
    Column {
        Options(
            alphabeticalScrollerIconId,
            filterOption.searchTitleId(),
            searchText,
            viewModel,

            ) {
            showAlphabeticalScroller.value = !showAlphabeticalScroller.value
        }

        AnimatedVisibility(showAlphabeticalScroller.value) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                AlphabeticalScroller(
                    characterList = characterList,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .background(AppColors.background),
                    onLetterSelected = { index ->
                        coroutineScope.launch {
                            listState.animateScrollToItem(index)
                        }
                    }
                )

                Divider()
            }
        }
    }
}

private fun listItemAction(
    addTrackToPlaylist: MutableState<Boolean>,
    navController: NavController<Destination>,
    track: Track,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    index: Int,
    viewModel: HomeViewModel
) {
    if (addTrackToPlaylist.value) {
        navController.navigate(
            Destination.Playlists(
                PlaylistViewMode.Add(track.id, track.title)
            )
        )

        addTrackToPlaylist.value = false
    } else {
        coroutineScope.launch(Dispatchers.Main) {
            listState.animateScrollToItem(index)
        }

        viewModel.playTrack(track)
    }
}

@Composable
private fun SideControls(
    context: Context,
    viewModel: HomeViewModel,
    navController: NavController<Destination>,
    modifier: Modifier,
    optionsIconId: Int,
    searchText: String,
    filterOption: FilterOptions,
    playMode: PlayMode,
    showAllOptions: Boolean,
    addTrackToPlaylist: () -> Unit,
    showSortOptions: () -> Unit,
    showOptions: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        ActionIconButton(optionsIconId) {
            showOptions()
        }

        AnimatedVisibility(showAllOptions) {
            Column {
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

                ActionIconButton(R.drawable.ic_reset) {
                    viewModel.clearSearch()
                    FolderAnalyzer.initTracksFromMusicFolder()
                    viewModel.initTrackList(null)
                    context.showToast(R.string.home_screen_reset_button_description)
                }
            }
        }
    }
}

@Composable
private fun AlphabeticalScroller(
    characterList: Map<Char, Int>,
    modifier: Modifier = Modifier,
    onLetterSelected: (index: Int) -> Unit,
) {
    Row(modifier = modifier) {
        characterList.keys.sorted().forEach { letter ->
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .clickable { onLetterSelected(characterList[letter] ?: 0) },
                color = AppColors.textColor
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Options(
    alphabeticalScrollerIconId: Int,
    searchPlaceholderId: Int,
    searchText: String,
    viewModel: HomeViewModel,
    toggleAlphabeticalScroller: () -> Unit,
) {
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
                        BodyMediumText(id = searchPlaceholderId)
                    }
                    innerTextField()
                },
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Max)
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(AppColors.unhighlight),
                cursorBrush = SolidColor(AppColors.unhighlight)
            )
        },
        actions = {
            AnimatedVisibility(searchText.isNotEmpty()) {
                ActionIconButton(R.drawable.ic_cancel) {
                    viewModel.clearSearch()
                    viewModel.initTrackList(null)
                }
            }

            ActionIconButton(alphabeticalScrollerIconId) {
                toggleAlphabeticalScroller()
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
