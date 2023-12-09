package com.coolnexttech.fireplayer.utils.extensions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.coolnexttech.fireplayer.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun getTopAppBarColor(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = AppColors.background,
        scrolledContainerColor = AppColors.background,
        navigationIconContentColor = AppColors.unhighlight,
        titleContentColor = AppColors.unhighlight,
        actionIconContentColor = AppColors.unhighlight
    )
}