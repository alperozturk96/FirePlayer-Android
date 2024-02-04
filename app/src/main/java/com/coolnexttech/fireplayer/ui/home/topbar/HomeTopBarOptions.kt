package com.coolnexttech.fireplayer.ui.home.topbar

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.ui.components.BodyMediumText
import com.coolnexttech.fireplayer.ui.components.button.ActionIconButton
import com.coolnexttech.fireplayer.ui.home.HomeViewModel
import com.coolnexttech.fireplayer.ui.theme.AppColors
import com.coolnexttech.fireplayer.utils.extensions.getTopAppBarColor
import com.coolnexttech.fireplayer.utils.extensions.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBarOptions(
    context: Context,
    alphabeticalScrollerIconId: Int,
    searchPlaceholderId: Int,
    searchText: String,
    viewModel: HomeViewModel,
    isPlaylistSelected: Boolean,
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
                }
            }

            if (!isPlaylistSelected) {
                ActionIconButton(R.drawable.ic_reset) {
                    viewModel.reset()
                    context.showToast(R.string.home_screen_reset_button_description)
                }

                ActionIconButton(alphabeticalScrollerIconId) {
                    toggleAlphabeticalScroller()
                }
            }
        }
    )
}