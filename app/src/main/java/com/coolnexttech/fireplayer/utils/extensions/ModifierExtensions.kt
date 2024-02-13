package com.coolnexttech.fireplayer.utils.extensions

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.blink(
    durationMillis: Int = 1000,
    fromAlpha: Float = 1f,
    toAlpha: Float = 0f
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val alpha by infiniteTransition.animateFloat(
        initialValue = fromAlpha,
        targetValue = toAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    this.then(
        Modifier.graphicsLayer(
            alpha = alpha
        )
    )
}
