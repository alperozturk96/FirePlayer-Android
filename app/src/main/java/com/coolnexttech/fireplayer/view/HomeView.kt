package com.coolnexttech.fireplayer.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.coolnexttech.fireplayer.viewModel.HomeViewModel

@Composable
fun HomeView(navController: NavController, viewModel: HomeViewModel) {
    val trackList by viewModel.trackList.collectAsState()

    Scaffold(topBar = { TopBar() }, bottomBar = { BottomBar() }) {
        LazyColumn(state = rememberLazyListState(), modifier = Modifier.padding(it)) {
            items(trackList) { track ->

            }
        }
    }
}

@Composable
private fun TopBar() {

}

@Composable
private fun BottomBar() {

}