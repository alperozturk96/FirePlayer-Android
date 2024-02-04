package com.coolnexttech.fireplayer.ui.components.bottomSheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.coolnexttech.fireplayer.ui.components.HeadlineMediumText
import com.coolnexttech.fireplayer.ui.theme.AppColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreActionsBottomSheet(
    title: String? = null,
    actions: List<Triple<Int, Int, () -> Unit>>,
    dismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        containerColor = AppColors.alternateBackground,
        onDismissRequest = {
            dismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp)
        ) {
            title?.let {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    HeadlineMediumText(text = title)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            actions.forEach { action ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    action.third()
                                }
                            }
                    },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = action.first),
                        contentDescription = "action icon",
                        tint = AppColors.textColor
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(action.second),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .padding(all = 8.dp),
                        color = AppColors.textColor
                    )
                }
            }
        }
    }
}