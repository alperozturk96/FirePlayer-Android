package com.coolnexttech.fireplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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

@Composable
fun ActionIconButton(id: Int, action: () -> Unit) {
    IconButton(action) {
        Icon(
            imageVector = ImageVector.vectorResource(id),
            tint = AppColors.unhighlight,
            contentDescription = "IconButton"
        )
    }
}

@Composable
fun ActionImageButton(id: Int, size: Dp = 30.dp, action: () -> Unit) {
    Image(
        painter = painterResource(id),
        contentDescription = null,
        modifier = Modifier
            .size(size)
            .clickable { action() })
}