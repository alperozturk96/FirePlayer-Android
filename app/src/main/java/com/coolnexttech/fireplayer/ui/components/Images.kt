package com.coolnexttech.fireplayer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun Drawable(id: Int, tint: Color? = null) {
    Image(
        painter = painterResource(id),
        modifier = Modifier.size(75.dp),
        contentDescription = "",
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}