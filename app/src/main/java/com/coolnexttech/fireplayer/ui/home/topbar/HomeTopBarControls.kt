package com.coolnexttech.fireplayer.ui.home.topbar

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.FilterOptions
import com.coolnexttech.fireplayer.model.PlayMode
import com.coolnexttech.fireplayer.ui.components.button.ActionIconButton
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.utils.extensions.showToast

@Composable
fun HomeTopBarControls(
    context: Context,
    viewModel: HomeViewModel,
    searchText: String,
    filterOption: FilterOptions,
    playMode: PlayMode,
    showSortOptions: () -> Unit,
    showSleepTimerAlertDialog: () -> Unit
) {
    Row {
        ActionIconButton(filterOption.filterOptionIconId()) {
            viewModel.changeFilterOption(searchText)

            context.showToast(
                filterOption.selectNextFilterOption().searchTitleId()
            )
        }

        ActionIconButton(R.drawable.ic_sleep_timer) {
            showSleepTimerAlertDialog()
        }

        ActionIconButton(playMode.getIconId()) {
            viewModel.changePlayMode()
        }

        ActionIconButton(R.drawable.ic_sort) {
            showSortOptions()
        }
    }
}