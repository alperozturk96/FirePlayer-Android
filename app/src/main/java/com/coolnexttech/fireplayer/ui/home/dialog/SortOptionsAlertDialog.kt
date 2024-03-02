package com.coolnexttech.fireplayer.ui.home.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.model.SortOptions

@Composable
fun SortOptionsAlertDialog(
    dismiss: () -> Unit,
    sort: (SortOptions) -> Unit,
) {
    Dialog({ dismiss() }) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FilledTonalButton(onClick = { sort(SortOptions.AToZ) }) {
                Text(text = stringResource(id = R.string.home_sort_dialog_sort_by_title_a_z))
            }

            FilledTonalButton(onClick = { sort(SortOptions.ZToA) }) {
                Text(text = stringResource(id = R.string.home_sort_dialog_sort_by_title_z_a))
            }

            FilledTonalButton(onClick = { sort(SortOptions.NewToOld) }) {
                Text(text = stringResource(id = R.string.home_sort_dialog_sort_by_new_to_old_title))
            }

            FilledTonalButton(onClick = { sort(SortOptions.OldToNew) }) {
                Text(text = stringResource(id = R.string.home_sort_dialog_sort_by_old_to_new_title))
            }

            TextButton(onClick = { dismiss() }) {
                Text(text = stringResource(id = R.string.common_cancel))
            }
        }
    }
}
