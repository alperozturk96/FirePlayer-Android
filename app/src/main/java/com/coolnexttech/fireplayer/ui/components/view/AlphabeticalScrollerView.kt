package com.coolnexttech.fireplayer.ui.components.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coolnexttech.fireplayer.ui.theme.AppColors

@Composable
fun AlphabeticalScrollerView(
    characterList: Map<Char, Int>,
    modifier: Modifier = Modifier,
    onLetterSelected: (index: Int) -> Unit,
) {
    Row(modifier = modifier) {
        characterList.keys.sorted().forEach { letter ->
            Text(
                text = letter.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 24.sp,
                modifier = Modifier
                    .clickable { onLetterSelected(characterList[letter] ?: 0) }
                    .padding(all = 8.dp),
                color = AppColors.textColor
            )
        }
    }
}