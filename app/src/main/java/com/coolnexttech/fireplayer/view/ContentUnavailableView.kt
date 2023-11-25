package com.coolnexttech.fireplayer.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.extensions.VSpacing16
import com.coolnexttech.fireplayer.extensions.VSpacing8
import com.coolnexttech.fireplayer.ui.components.Drawable
import com.coolnexttech.fireplayer.ui.components.HeadlineMediumText
import com.coolnexttech.fireplayer.ui.components.HeadlineSmallText
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun ContentUnavailableView(titleSuffix: String?, title: String? = null) {
    val titleText = title
        ?: (stringResource(id = R.string.content_unavailable_title_prefix) + " " + titleSuffix)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Drawable(R.drawable.ic_search, AppColors.unhighlight)

        VSpacing16()

        HeadlineMediumText(
            titleText,
            AppColors.unhighlight
        )

        if (title == null) {
            VSpacing8()
            HeadlineSmallText(R.string.content_unavailable_description, AppColors.unhighlight)
        }
    }
}