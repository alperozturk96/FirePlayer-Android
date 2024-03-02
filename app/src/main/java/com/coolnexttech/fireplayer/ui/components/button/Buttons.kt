package com.coolnexttech.fireplayer.ui.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun ActionIconButton(id: Int, modifier: Modifier = Modifier, action: () -> Unit) {
    IconButton(action, modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(id),
            tint = AppColors.unhighlight,
            contentDescription = "IconButton"
        )
    }
}
