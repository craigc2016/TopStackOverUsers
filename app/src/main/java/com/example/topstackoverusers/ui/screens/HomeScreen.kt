package com.example.topstackoverusers.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.topstackoverusers.R
import com.example.topstackoverusers.StackOverFlowApplication
import com.example.topstackoverusers.data.remote.models.StackOverFlowItem
import com.example.topstackoverusers.viewmodel.UiState

@Composable
fun HomeScreen() {

    val appContainer = (LocalContext.current.applicationContext as StackOverFlowApplication).appContainer
    val viewModel = appContainer.homeViewModel
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(val uiState = state) {
            is UiState.Success -> {
                HomeContent(state = uiState)
            }
            is UiState.Error -> Text(text = "Error")
            is UiState.Loading -> Text(text = "Loading")
        }
    }
}

@Composable
private fun HomeContent(
    state: UiState.Success
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = rememberLazyListState(),
        ) {
            items(state.data) { item ->
                ListItem(item)
            }
        }
    }

}

@Composable
private fun ListItem(
    item: StackOverFlowItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = MaterialTheme.shapes.large,
        onClick = { }
    ) {
        Row{
            Image(
               painter = painterResource(id = R.drawable.placeholder),
                contentDescription = "Profile picture",
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = item.displayName)
                Text(text = item.reputation.toString())
            }
        }
    }
}