package com.coolnexttech.fireplayer.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun DialogButton(id: Int, action: () -> Unit) {
    Button(action) {
        Text(
            text = stringResource(id),
            color = AppColors.textColor
        )
    }
}