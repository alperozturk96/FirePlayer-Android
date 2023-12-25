package com.coolnexttech.fireplayer.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun BodyMediumText(id: Int) {
    Text(
        text = stringResource(id = id),
        style = MaterialTheme.typography.bodyMedium.copy(AppColors.unhighlight),
    )
}

@Composable
fun HeadlineMediumText(text: String, color: Color = AppColors.textColor) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
}

@Composable
fun HeadlineSmallText(id: Int, color: Color = AppColors.textColor) {
    Text(
        text = stringResource(id),
        color = color,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = TextAlign.Center
    )
}
