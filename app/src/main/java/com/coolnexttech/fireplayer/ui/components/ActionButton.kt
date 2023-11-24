package com.coolnexttech.fireplayer.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun ActionButton(id: Int, action: () -> Unit) {
    IconButton(action) {
        Icon(
            imageVector = ImageVector.vectorResource(id),
            tint = AppColors.unHighlight,
            contentDescription = "IconButton"
        )
    }
}