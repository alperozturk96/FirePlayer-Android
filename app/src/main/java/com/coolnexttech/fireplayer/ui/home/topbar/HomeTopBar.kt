package com.coolnexttech.fireplayer.ui.home.topbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.components.view.AlphabeticalScrollerView
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeTopBar(
    viewModel: HomeViewModel,
    characterList: Map<Char, Int>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    showSortOptions: () -> Unit,
    showSleepTimerAlertDialog: () -> Unit
) {
    val context = LocalContext.current
    val searchText by viewModel.searchText.collectAsState()
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

        AnimatedVisibility(showAlphabeticalScroller.value) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
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

                if (searchText.isEmpty()) {
                    AlphabeticalScrollerView(
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
                }

                Divider()
            }
        }
    }
}
