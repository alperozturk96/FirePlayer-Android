package com.coolnexttech.fireplayer.ui.home.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.Track
import com.coolnexttech.fireplayer.ui.components.view.AlphabeticalScrollerView
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeTopBar(
    viewModel: HomeViewModel,
    filteredTracks: List<Track>,
    characterList: Map<Char, Int>,
    coroutineScope: CoroutineScope,
    searchText: String,
    listState: LazyListState,
    showSortOptions: () -> Unit,
    showSleepTimerAlertDialog: () -> Unit
) {
    val context = LocalContext.current
    val playMode by viewModel.playMode.collectAsState()
    val filterOption by viewModel.filterOption.collectAsState()
    val showAlphabeticalScroller = remember { mutableStateOf(false) }
    val alphabeticalScrollerIconId = if (showAlphabeticalScroller.value) {
        R.drawable.ic_arrow_up
    } else {
        R.drawable.ic_arrow_down
    }

    Column {
        HomeTopBarOptions(
            context,
            alphabeticalScrollerIconId,
            filterOption.searchTitleId(),
            searchText,
            viewModel,
        ) {
            showAlphabeticalScroller.value = !showAlphabeticalScroller.value
        }

        AnimatedVisibility(showAlphabeticalScroller.value || searchText.isNotEmpty()) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                HomeTopBarControls(
                    context,
                    viewModel,
                    searchText,
                    filterOption,
                    playMode,
                    showSortOptions = showSortOptions,
                    showSleepTimerAlertDialog = showSleepTimerAlertDialog
                )

                Text(
                    text = stringResource(id = R.string.track_list_size, filteredTracks.size),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp)
                        .background(AppColors.background)
                )

                if (searchText.isEmpty()) {
                    AlphabeticalScrollerView(
                        characterList = characterList,
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(start = 8.dp)
                            .background(AppColors.background),
                        onLetterSelected = { index ->
                            coroutineScope.launch {
                                listState.animateScrollToItem(index)
                            }
                        }
                    )
                }

                HorizontalDivider(color = AppColors.textColor)
            }
        }
    }
}
