package com.coolnexttech.fireplayer.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.model.ActionIcon
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItemText(
    text: String,
    color: Color = AppColors.textColor,
    action: () -> Unit,
    endAction: ActionIcon? = null,
) {
    Row(Modifier.clickable { action() }) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
                .padding(all = 8.dp),
            color = color
        )

        Spacer(modifier = Modifier.weight(1f))

        endAction?.let {
            ActionIconButton(it.iconId) {
                it.action()
            }
        }
    }

    Divider()
}
