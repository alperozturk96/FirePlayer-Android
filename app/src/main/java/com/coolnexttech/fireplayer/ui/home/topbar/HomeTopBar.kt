package com.coolnexttech.fireplayer.ui.home.topbar

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.ui.components.view.AlphabeticalScrollerView
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.navigation.Destination
import com.coolnexttech.fireplayer.ui.theme.AppColors
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeTopBar(
    context: Context,
    navController: NavController<Destination>,
    playMode: PlayMode,
    alphabeticalScrollerIconId: Int,
    filterOption: FilterOptions,
    searchText: String,
    viewModel: HomeViewModel,
    showAlphabeticalScroller: MutableState<Boolean>,
    characterList: Map<Char, Int>,
    coroutineScope: CoroutineScope,
    listState: LazyListState,
    addTrackToPlaylist: () -> Unit,
    showSortOptions: () -> Unit,
) {
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
                    navController,
                    searchText,
                    filterOption,
                    playMode,
                    addTrackToPlaylist = addTrackToPlaylist,
                    showSortOptions = showSortOptions)

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

                Divider()
            }
        }
    }
}