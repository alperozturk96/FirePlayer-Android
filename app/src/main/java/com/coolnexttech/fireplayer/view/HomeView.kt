package com.coolnexttech.fireplayer.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.navigation.NavController
import com.coolnexttech.fireplayer.R
import com.coolnexttech.fireplayer.util.FolderAnalyzer
import com.coolnexttech.fireplayer.viewModel.HomeViewModel

@Composable
fun HomeView(navController: NavController, viewModel: HomeViewModel) {
    val context = LocalContext.current
    val folderAnalyzer = FolderAnalyzer(context)
    val trackList by viewModel.trackList.collectAsState()

    Scaffold(topBar = { TopBar() }, bottomBar = { BottomBar() }) {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(it)) {
            items(trackList) { track ->

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar() {
    TopAppBar(
        title = { /* Add title if needed */ },
        actions = {
            IconButton(onClick = {

            }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_folder),
                    contentDescription = "Browse folder"
                )
            }
        }
    )
}

@Composable
private fun BottomBar() {

}